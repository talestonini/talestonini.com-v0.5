package com.talestonini.db

import cats.effect.{IO, Resource}
import cats.effect.unsafe.implicits.global
import com.talestonini.db.model._
import com.talestonini.utils.randomAlphaNumericString
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import monix.execution.Scheduler.Implicits.{global => scheduler}
import org.http4s.{Entity, EntityDecoder, EntityEncoder, Headers, Header, Method, Request}
import org.http4s.circe._
import org.http4s.client._
import org.http4s.ember.client._
import org.http4s.implicits._
import org.http4s.{Uri, UriTemplate}
import org.http4s.UriTemplate._
import org.typelevel.ci._
import scala.concurrent._
import scala.util.{Failure, Success}

object CloudFirestore {

  // private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY" // restricted web app API key
  private val ApiKey        = "AIzaSyDECffVp14r01OmpTI8uFh3K5S6u3Anigo"
  private val ProjectId     = "ttdotcom"
  private val Database      = "(default)"
  private val FirestoreHost = "firestore.googleapis.com"

  case class CloudFirestoreException(msg: String) extends Exception(msg)

  def getPosts(token: String): Future[Docs[Post]] =
    getDocuments[Post](token, s"projects/$ProjectId/databases/$Database/documents/posts")

  def getPosts(): Future[Docs[Post]] =
    getDocuments[Post](s"projects/$ProjectId/databases/$Database/documents/posts")

  def getComments(token: String, postDocName: String): Future[Docs[Comment]] =
    getDocuments[Comment](token, s"$postDocName/comments")

  def getComments(postDocName: String): Future[Docs[Comment]] =
    getDocuments[Comment](s"$postDocName/comments")

  def createComment(token: String, postDocName: String, comment: Comment): Future[Doc[Comment]] = {
    val newCommentId   = randomAlphaNumericString(20)
    val commentDocName = s"$postDocName/comments/$newCommentId"
    upsertDocument[Comment](token, commentDocName, comment)
  }

  def createComment(postDocName: String, comment: Comment): Future[Doc[Comment]] = {
    val newCommentId   = randomAlphaNumericString(20)
    val commentDocName = s"$postDocName/comments/$newCommentId"
    upsertDocument[Comment](commentDocName, comment)
  }

  def removeComment(token: String, path: String): Future[Option[Throwable]] =
    deleteDocument[Comment](token, path)

  def removeComment(path: String): Future[Option[Throwable]] =
    deleteDocument[Comment](path)

  // -------------------------------------------------------------------------------------------------------------------

  def getAuthTokenF(): IO[String] = {
    val uri     = uri"https://identitytoolkit.googleapis.com/v1/accounts:signUp".withQueryParam("key", ApiKey)
    val request = Request[IO](Method.POST, uri).withHeaders(Headers(Header.Raw(ci"Content-Type", "application/json")))
    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

    clientResource
      .use(client =>
        client
          .expectOr[AuthTokenResponse](request)(response =>
            IO(CloudFirestoreException(s"failed requesting signUp token: $response")))
          .map(response => response.idToken))
  }

  def getAuthToken(): Future[String] = getAuthTokenF().unsafeToFuture()

  def getDocumentsF[M <: Model](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[M]]
  ): IO[Docs[M]] = {
    val uri     = toFirestoreUri(path)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))
    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

    clientResource
      .use(client =>
        client
          .expectOr[DocsRes[M]](request)(response =>
            IO(CloudFirestoreException(s"failed getting documents: $response")))
          .map(docsRes => docsRes.documents.sortBy(_.fields.sortingField).reverse))
  }

  private def getDocuments[M <: Model](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[M]]
  ): Future[Docs[M]] = getDocumentsF(token, path).unsafeToFuture()

  private def getDocuments[M <: Model](path: String)(
    implicit docsResDecoder: Decoder[DocsRes[M]]
  ): Future[Docs[M]] = {
    val p = Promise[Docs[M]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith getDocuments[M](token.get, path)
        case f: Failure[String] =>
          val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })
    p.future
  }

  def upsertDocumentF[M <: Model](token: String, path: String, model: M)(
    implicit docDecoder: Decoder[Doc[M]], bodyEncoder: Encoder[Body[M]]
  ): IO[Doc[M]] = {
    val uri = toFirestoreUri(path).withQueryParam("updateMask.fieldPaths", model.dbFields)
    val request = Request[IO](Method.PATCH, uri)
      .withEntity[Json](Body(path, model).asJson)
      .withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))
    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

    clientResource
      .use(client =>
        client
          .expectOr[Doc[M]](request)(response => IO(CloudFirestoreException(s"failed upserting document: $response"))))
  }

  private def upsertDocument[M <: Model](token: String, path: String, model: M)(
    implicit docDecoder: Decoder[Doc[M]], bodyEncoder: Encoder[Body[M]]
  ): Future[Doc[M]] =
    if (isBadRequest(token, model.content)) {
      // TODO improve this
      val p = Promise[Doc[M]]()
      p failure CloudFirestoreException("")
      p.future
    } else
      upsertDocumentF(token, path, model).unsafeToFuture()

  private def upsertDocument[M <: Model](path: String, model: M)(
    implicit docDecoder: Decoder[Doc[M]], bodyEncoder: Encoder[Body[M]]
  ): Future[Doc[M]] = {
    val p = Promise[Doc[M]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith upsertDocument[M](token.get, path, model)
        case f: Failure[String] =>
          val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })
    p.future
  }

  def deleteDocumentF[M <: Model](token: String, path: String)(
    implicit docDecoder: Decoder[Doc[M]]
  ): IO[Option[Throwable]] = {
    val uri = toFirestoreUri(path)
    val request = Request[IO](method = Method.DELETE, uri = uri)
      .withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))
    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

    clientResource
      .use(client => client.successful(request))
      .map {
        case true  => None
        case false => Some(CloudFirestoreException("failed deleting document"))
      }
  }

  def deleteDocument[M <: Model](token: String, path: String)(
    implicit docDecoder: Decoder[Doc[M]]
  ): Future[Option[Throwable]] = deleteDocumentF(token, path).unsafeToFuture()

  private def deleteDocument[M <: Model](path: String)(
    implicit docDecoder: Decoder[Doc[M]]
  ): Future[Option[Throwable]] = {
    val p = Promise[Option[Throwable]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith deleteDocumentF[M](token.get, path).unsafeToFuture()
        case f: Failure[String] =>
          val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })
    p.future
  }

  private def toFirestoreUri(path: String): Uri =
    UriTemplate(
      authority = Some(Uri.Authority(host = Uri.RegName(FirestoreHost))),
      scheme = Some(Uri.Scheme.https),
      path = List(PathElm("v1"), PathElm(path))
    ).toUriIfPossible.getOrElse(throw CloudFirestoreException("unable to build URI"))

  // map of token -> (last usage time, last usage content)
  private var antiHackCache: Map[String, (Long, String)] = Map.empty

  private def isBadRequest(token: String, content: String): Boolean = {
    val now                 = java.lang.System.currentTimeMillis()
    val (luTime, luContent) = antiHackCache.get(token).getOrElse((0L, ""))

    // update the cache
    antiHackCache = antiHackCache.updated(token, (now, content))

    val isBadInterval    = luTime > 0L && (now - luTime) < 1000
    val isSimilarContent = luContent.nonEmpty && content.toSeq.diff(luContent).unwrap.length() < 3
    isBadInterval || isSimilarContent
  }

}
