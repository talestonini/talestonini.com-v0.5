package com.talestonini.db

import com.talestonini.db.model._
import com.talestonini.utils.randomAlphaNumericString
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method.{GET, DELETE, PATCH, POST}
import fr.hmil.roshttp.Protocol.HTTPS
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import monix.execution.Scheduler.Implicits.{global => scheduler}
import scala.concurrent._
import scala.util.{Failure, Success}
import sttp.client3._
import sttp.client3.circe._
import com.talestonini
import org.scalajs.dom.experimental.RequestMode
import org.scalajs.dom.experimental.RequestCredentials

object CloudFirestore {

  private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY" // restricted web app API key
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

  def removeComment(token: String, path: String): Future[Option[Throwable]] = {
    deleteDocument[Comment](token, path)
  }

  def removeComment(path: String): Future[Option[Throwable]] = {
    deleteDocument[Comment](path)
  }

  def getAuthToken(): Future[String] = {
    val request: Request[AuthTokenResponseBody, Any] = basicRequest
      .post(uri"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$ApiKey")
      .header("Content-Type", "application/json")
      .response(asJson[AuthTokenResponseBody].getRight)

    val p = Promise[String]()
    request.send(FetchBackend()) onComplete {
      case s: Success[Response[AuthTokenResponseBody]] =>
        p success s.value.body.idToken
      case f: Failure[Response[AuthTokenResponseBody]] =>
        var errMsg = s"failed requesting signUp token: ${f.exception.getMessage()}"
        p failure CloudFirestoreException(errMsg)
    }
    p.future
  }

  // -------------------------------------------------------------------------------------------------------------------

  private val backend = FetchBackend(FetchOptions(Some(RequestCredentials.include), Some(RequestMode.cors)))
  //private val backend = FetchBackend()

  private def getDocuments[E <: Entity](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[E]]
  ): Future[Docs[E]] = {
    val request: Request[DocsRes[E], Any] = basicRequest
      .get(uri"https://$FirestoreHost/v1/$path")
      .header("Authorization", s"Bearer $token")
      .response(asJson[DocsRes[E]].getRight)
      .followRedirects(true)

    val p = Promise[Docs[E]]()
    request.send(backend) onComplete {
      case s: Success[Response[DocsRes[E]]] =>
        p success s.value.body.documents.sortBy(_.fields.sortingField).reverse
      case f: Failure[Response[DocsRes[E]]] =>
        val errMsg = s"failed getting documents: ${f.exception.getMessage()}"
        p failure CloudFirestoreException(errMsg)
    }
    p.future
  }

  private def getDocuments[E <: Entity](path: String)(
    implicit docsResDecoder: Decoder[DocsRes[E]]
  ): Future[Docs[E]] = {
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

  private def upsertDocument[E <: Entity](token: String, path: String, entity: E)(
    implicit docDecoder: Decoder[Doc[E]]
  ): Future[Doc[E]] = {

    def queryParams(): String =
      (for (dbField <- entity.dbFields) yield (s"updateMask=$dbField")).mkString("&")

    val p = Promise[Doc[E]]()

    //if (isBadRequest(token, entity.content))
    //p failure CloudFirestoreException("")
    //else {
    //val request: Request[Doc[E], Any] = basicRequest
    //.post(uri"http://$FirestoreHost/v1/$path?${queryParams()}")
    //.body(entityToDocBody(path, entity))
    //.response(asJson[Doc[E]].getRight)
    //}

    HttpRequest()
      .withMethod(PATCH)
      .withProtocol(HTTPS)
      .withHost(FirestoreHost)
      .withPath(s"/v1/$path")
      .withQueryParameters((for (dbField <- entity.dbFields) yield ("updateMask.fieldPaths", dbField)): _*)
      .withHeader("Authorization", s"Bearer $token")
      .withBody(entityToDocBody(path, entity))
      .send()
      .onComplete({
        case rawJson: Success[SimpleHttpResponse] =>
          decode[Doc[E]](rawJson.get.body) match {
            case Left(e) =>
              val errMsg = s"unable to decode response from patch document: ${e.getMessage()}"
              p failure CloudFirestoreException(errMsg)
            case Right(doc) =>
              p success doc
          }
        case f: Failure[SimpleHttpResponse] =>
          val errMsg = s"failed upserting document: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })

    p.future
  }

  private def upsertDocument[E <: Entity](path: String, entity: E)(
    implicit docDecoder: Decoder[Doc[E]]
  ): Future[Doc[E]] = {
    val p = Promise[Doc[E]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith upsertDocument[E](token.get, path, entity)
        case f: Failure[String] =>
          val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })
    p.future
  }

  private def deleteDocument[E <: Entity](token: String, path: String)(
    implicit docDecoder: Decoder[Doc[E]]
  ): Future[Option[Throwable]] = {
    val p = Promise[Option[Throwable]]()
    HttpRequest()
      .withMethod(DELETE)
      .withProtocol(HTTPS)
      .withHost(FirestoreHost)
      .withPath(s"/v1/$path")
      .withHeader("Authorization", s"Bearer $token")
      .send()
      .onComplete({
        case empty: Success[SimpleHttpResponse] =>
          p success None
        case f: Failure[SimpleHttpResponse] =>
          val errMsg = s"failed deleting document: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })

    p.future
  }

  private def deleteDocument[E <: Entity](path: String)(
    implicit docDecoder: Decoder[Doc[E]]
  ): Future[Option[Throwable]] = {
    val p = Promise[Option[Throwable]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith deleteDocument[E](token.get, path)
        case f: Failure[String] =>
          val errMsg = s"failed getting auth token: ${f.exception.getMessage()}"
          p failure CloudFirestoreException(errMsg)
      })
    p.future
  }

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
