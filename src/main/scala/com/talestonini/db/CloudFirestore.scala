package com.talestonini.db

import cats.effect.IO
import com.talestonini.db.model._
import com.talestonini.utils.randomAlphaNumericString
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{Entity, EntityDecoder, EntityEncoder, Headers, Header, Method, Request}
import org.http4s.circe._
import org.http4s.client._
import org.http4s.dom.FetchClientBuilder
import org.http4s.implicits._
import org.http4s.{Uri, UriTemplate}
import org.http4s.UriTemplate._
import org.typelevel.ci._

object CloudFirestore extends Database[IO] {

  private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY" // restricted web app API key
  private val ProjectId     = "ttdotcom"
  private val Database      = "(default)"
  private val FirestoreHost = "firestore.googleapis.com"

  case class CloudFirestoreException(msg: String) extends Exception(msg)

  def getPosts(token: String): IO[Docs[Post]] =
    getDocuments[Post](token, s"projects/$ProjectId/databases/$Database/documents/posts")

  def getPosts(): IO[Docs[Post]] =
    getDocuments[Post, IO](this, s"projects/$ProjectId/databases/$Database/documents/posts")

  def getComments(token: String, postDocName: String): IO[Docs[Comment]] =
    getDocuments[Comment](token, s"$postDocName/comments")

  def getComments(postDocName: String): IO[Docs[Comment]] =
    getDocuments[Comment, IO](this, s"$postDocName/comments")

  def createComment(token: String, postDocName: String, comment: Comment): IO[Doc[Comment]] = {
    val newCommentId   = randomAlphaNumericString(20)
    val commentDocName = s"$postDocName/comments/$newCommentId"
    upsertDocument[Comment](token, commentDocName, comment)
  }

  def createComment(postDocName: String, comment: Comment): IO[Doc[Comment]] = {
    val newCommentId   = randomAlphaNumericString(20)
    val commentDocName = s"$postDocName/comments/$newCommentId"
    upsertDocument[Comment, IO](this, commentDocName, comment)
  }

  def removeComment(token: String, path: String): IO[Option[Throwable]] =
    deleteDocument[Comment](token, path)

  def removeComment(path: String): IO[Option[Throwable]] =
    deleteDocument[Comment, IO](this, path)

  // -------------------------------------------------------------------------------------------------------------------

  def getAuthToken(): IO[String] = {
    val uri     = uri"https://identitytoolkit.googleapis.com/v1/accounts:signUp".withQueryParam("key", ApiKey)
    val request = Request[IO](Method.POST, uri).withHeaders(Headers(Header.Raw(ci"Content-Type", "application/json")))

    FetchClientBuilder[IO].create
      .expectOr[AuthTokenResponse](request)(response =>
        IO(CloudFirestoreException(s"failed requesting signUp token: $response")))
      .map(response => response.idToken)
  }

  // def getAuthToken(): Future[String] = getAuthTokenF().unsafeToFuture()

  def getDocuments[T <: Model](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[T]]
  ): IO[Docs[T]] = {
    val uri     = toFirestoreUri(path)
    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))

    FetchClientBuilder[IO].create
      .expectOr[DocsRes[T]](request)(response => IO(CloudFirestoreException(s"failed getting documents: $response")))
      .map(docsRes => docsRes.documents.sortBy(_.fields.sortingField).reverse)
  }

  // private def getDocuments[T <: Model](token: String, path: String)(
  // implicit docsResDecoder: Decoder[DocsRes[T]]
  // ): Future[Docs[T]] = getDocumentsF(token, path).unsafeToFuture()

  // private def getDocuments[T <: Model](path: String)(
  // implicit docsResDecoder: Decoder[DocsRes[T]]
  // ): Future[Docs[T]] = {
  // val p = Promise[Docs[T]]()
  // getAuthToken()
  // .onComplete({
  // case token: Success[String] =>
  // p completeWith getDocuments[T](token.get, path)
  // case f: Failure[String] =>
  // val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
  // p failure CloudFirestoreException(errMsg)
  // })
  // p.future
  // }

  def upsertDocument[T <: Model](token: String, path: String, model: T)(
    implicit docDecoder: Decoder[Doc[T]], bodyEncoder: Encoder[Body[T]]
  ): IO[Doc[T]] = {
    val uri = toFirestoreUri(path).withQueryParam("updateMask.fieldPaths", model.dbFields)
    val request = Request[IO](Method.PATCH, uri)
      .withEntity[Json](Body(path, model).asJson)
      .withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))

    FetchClientBuilder[IO].create
      .expectOr[Doc[T]](request)(response => IO(CloudFirestoreException(s"failed upserting document: $response")))
  }

  // private def upsertDocument[T <: Model](token: String, path: String, model: T)(
  // implicit docDecoder: Decoder[Doc[T]], bodyEncoder: Encoder[Body[T]]
  // ): Future[Doc[T]] =
  // if (isBadRequest(token, model.content)) {
  // Promise.failed[Doc[T]](CloudFirestoreException("")).future
  // } else
  // upsertDocumentF(token, path, model).unsafeToFuture()

  // private def upsertDocument[T <: Model](path: String, model: T)(
  // implicit docDecoder: Decoder[Doc[T]], bodyEncoder: Encoder[Body[T]]
  // ): Future[Doc[T]] = {
  // val p = Promise[Doc[T]]()
  // getAuthToken()
  // .onComplete({
  // case token: Success[String] =>
  // p completeWith upsertDocument[T](token.get, path, model)
  // case f: Failure[String] =>
  // val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
  // p failure CloudFirestoreException(errMsg)
  // })
  // p.future
  // }

  def deleteDocument[T <: Model](token: String, path: String)(
    implicit docDecoder: Decoder[Doc[T]]
  ): IO[Option[Throwable]] = {
    val uri = toFirestoreUri(path)
    val request = Request[IO](method = Method.DELETE, uri = uri)
      .withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))

    FetchClientBuilder[IO].create
      .successful(request)
      .map {
        case true  => None
        case false => Some(CloudFirestoreException("failed deleting document"))
      }
  }

  // def deleteDocument[T <: Model](token: String, path: String)(
  // implicit docDecoder: Decoder[Doc[T]]
  // ): Future[Option[Throwable]] = deleteDocumentF(token, path).unsafeToFuture()

  // private def deleteDocument[T <: Model](path: String)(
  // implicit docDecoder: Decoder[Doc[T]]
  // ): Future[Option[Throwable]] = {
  // val p = Promise[Option[Throwable]]()
  // getAuthToken()
  // .onComplete({
  // case token: Success[String] =>
  // p completeWith deleteDocumentF[T](token.get, path).unsafeToFuture()
  // case f: Failure[String] =>
  // val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
  // p failure CloudFirestoreException(errMsg)
  // })
  // p.future
  // }

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
