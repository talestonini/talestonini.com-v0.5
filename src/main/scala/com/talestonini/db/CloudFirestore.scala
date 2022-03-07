package com.talestonini.db

import cats.effect.{IO, Resource}
import cats.effect.unsafe.implicits.global
import com.talestonini.db.model._
import com.talestonini.utils.randomAlphaNumericString
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import org.http4s.{Entity => Ent, EntityDecoder, Headers, Header, Method, Request}
import org.http4s.client._
import org.http4s.circe._
import org.http4s.ember.client._
import org.http4s.implicits._
import org.http4s.UriTemplate
import org.http4s.UriTemplate._
import org.http4s.Uri
import org.typelevel.ci._
import scala.concurrent._
import scala.util.{Failure, Success}

object CloudFirestore {

  //private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY" // restricted web app API key
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
    Promise[Doc[Comment]]().future
    //upsertDocument[Comment](token, commentDocName, comment)
  }

  def createComment(postDocName: String, comment: Comment): Future[Doc[Comment]] = {
    val newCommentId   = randomAlphaNumericString(20)
    val commentDocName = s"$postDocName/comments/$newCommentId"
    Promise[Doc[Comment]]().future
    //upsertDocument[Comment](commentDocName, comment)
  }

  def removeComment(token: String, path: String): Future[Option[Throwable]] =
    Promise[Option[Throwable]]().future
  //deleteDocument[Comment](token, path)

  def removeComment(path: String): Future[Option[Throwable]] =
    Promise[Option[Throwable]]().future
  //deleteDocument[Comment](path)

  def getAuthToken(): Future[String] = getAuthTokenF().unsafeToFuture()

  def getAuthTokenF(): IO[String] = {
    implicit val entityDecoder: EntityDecoder[IO, AuthTokenResponse] = jsonOf[IO, AuthTokenResponse]

    val uri = uri"https://identitytoolkit.googleapis.com/v1/accounts:signUp".withQueryParam("key", ApiKey)

    val request = Request[IO](Method.POST, uri).withHeaders(Headers(Header.Raw(ci"Content-Type", "application/json")))

    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

    clientResource
      .use(client =>
        client
          .expectOr[AuthTokenResponse](request)(response =>
            IO(CloudFirestoreException(s"failed requesting signUp token: ${response}"))
          )
          .map(response => response.idToken)
      )
  }

  // -------------------------------------------------------------------------------------------------------------------

  private def getDocuments[E <: Entity](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[E]]
  ): Future[Docs[E]] = {
    implicit val entityDecoder: EntityDecoder[IO, DocsRes[E]] = jsonOf[IO, DocsRes[E]]

    val uri = UriTemplate(
      authority = Some(Uri.Authority(host = Uri.RegName(FirestoreHost))),
      scheme = Some(Uri.Scheme.https),
      path = List(PathElm("v1"), PathElm(path))
    ).toUriIfPossible.getOrElse(throw CloudFirestoreException("unable to build URI to get documents"))

    val request = Request[IO](Method.GET, uri).withHeaders(Header.Raw(CIString("Authorization"), s"Bearer $token"))

    val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

    clientResource
      .use(client =>
        client
          .expectOr[
              DocsRes[E]](request)(response => IO(CloudFirestoreException(s"failed getting documents: ${response}")))
          .map(docsRes => docsRes.documents.sortBy(_.fields.sortingField).reverse)
      )
      .unsafeToFuture()
  }

  private def getDocuments[E <: Entity](path: String)(
    implicit docsResDecoder: Decoder[DocsRes[E]]
  ): Future[Docs[E]] = {
    //import scala.concurrent.ExecutionContext.Implicits.global
    import monix.execution.Scheduler.Implicits.{global => scheduler}

    val p = Promise[Docs[E]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith getDocuments[E](token.get, path)
        case f: Failure[String] =>
          val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })
    p.future
  }

  //private def upsertDocument[E <: Entity](token: String, path: String, entity: E)(
  //implicit docDecoder: Decoder[Doc[E]]
  //): Future[Doc[E]] = {
  //val p = Promise[Doc[E]]()

  //if (isBadRequest(token, entity.content))
  //p failure CloudFirestoreException("")
  //else
  //Future {
  //HttpRequest()
  //.withMethod(PATCH)
  //.withProtocol(HTTPS)
  //.withHost(FirestoreHost)
  //.withPath(s"/v1/$path")
  //.withQueryParameters((for (dbField <- entity.dbFields) yield ("updateMask.fieldPaths", dbField)): _*)
  //.withHeader("Authorization", s"Bearer $token")
  //.withBody(entityToDocBody(path, entity))
  //.send()
  //.onComplete({
  //case rawJson: Success[SimpleHttpResponse] =>
  //decode[Doc[E]](rawJson.get.body) match {
  //case Left(e) =>
  //val errMsg = s"unable to decode response from patch document: ${e.getMessage()}"
  //p failure CloudFirestoreException(errMsg)
  //case Right(doc) =>
  //p success doc
  //}
  //case f: Failure[SimpleHttpResponse] =>
  //val errMsg = s"failed upserting document: ${f.exception.getMessage()}"
  //p failure CloudFirestoreException(errMsg)
  //})
  //}

  //p.future
  //}

  //private def upsertDocument[E <: Entity](path: String, entity: E)(
  //implicit docDecoder: Decoder[Doc[E]]
  //): Future[Doc[E]] = {
  //val p = Promise[Doc[E]]()
  //getAuthToken()
  //.onComplete({
  //case token: Success[String] =>
  //p completeWith upsertDocument[E](token.get, path, entity)
  //case f: Failure[String] =>
  //val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
  //p failure CloudFirestoreException(errMsg)
  //})
  //p.future
  //}

  //private def deleteDocument[E <: Entity](token: String, path: String)(
  //implicit docDecoder: Decoder[Doc[E]]
  //): Future[Option[Throwable]] = {
  //val p = Promise[Option[Throwable]]()
  //Future {
  //HttpRequest()
  //.withMethod(DELETE)
  //.withProtocol(HTTPS)
  //.withHost(FirestoreHost)
  //.withPath(s"/v1/$path")
  //.withHeader("Authorization", s"Bearer $token")
  //.send()
  //.onComplete({
  //case empty: Success[SimpleHttpResponse] =>
  //p success None
  //case f: Failure[SimpleHttpResponse] =>
  //val errMsg = s"failed deleting document: ${f.exception.getMessage()}"
  //p failure CloudFirestoreException(errMsg)
  //})
  //}

  //p.future
  //}

  //private def deleteDocument[E <: Entity](path: String)(
  //implicit docDecoder: Decoder[Doc[E]]
  //): Future[Option[Throwable]] = {
  //val p = Promise[Option[Throwable]]()
  //getAuthToken()
  //.onComplete({
  //case token: Success[String] =>
  //p completeWith deleteDocument[E](token.get, path)
  //case f: Failure[String] =>
  //val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
  //p failure CloudFirestoreException(errMsg)
  //})
  //p.future
  //}

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
