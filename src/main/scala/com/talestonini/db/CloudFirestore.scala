package com.talestonini.db

import cats.syntax.either._
import com.talestonini.db.model._
import com.talestonini.utils.randomAlphaNumericString
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method.{GET, PATCH, POST}
import fr.hmil.roshttp.Protocol.HTTPS
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe._
import io.circe.parser._
import monix.execution.Scheduler.Implicits.{global => scheduler}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object CloudFirestore {

  private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY"
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

  def getAuthToken(): Future[String] = {
    val p = Promise[String]()
    Future {
      HttpRequest()
        .withMethod(POST)
        .withProtocol(HTTPS)
        .withHost("identitytoolkit.googleapis.com")
        .withPath("/v1/accounts:signUp")
        .withQueryParameter("key", ApiKey)
        .send()
        .onComplete({
          case rawJson: Success[SimpleHttpResponse] =>
            val json = parse(rawJson.get.body).getOrElse(Json.Null)
            json.hcursor.get[String]("idToken") match {
              case Left(e) =>
                var errMsg = s"unable to decode response from signUp: ${e.getMessage()}"
                p failure CloudFirestoreException(errMsg)
              case Right(token) =>
                p success token
            }
          case f: Failure[SimpleHttpResponse] =>
            var errMsg = s"failed requesting signUp token: ${f.exception.getMessage()}"
            p failure CloudFirestoreException(errMsg)
        })
    }
    p.future
  }

  // -------------------------------------------------------------------------------------------------------------------

  private def getDocuments[E <: Entity](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[E]]
  ): Future[Docs[E]] = {
    val p = Promise[Docs[E]]()
    Future {
      HttpRequest()
        .withMethod(GET)
        .withProtocol(HTTPS)
        .withHost(FirestoreHost)
        .withPath("/v1/" + path)
        .withHeader("Authorization", s"Bearer $token")
        .send()
        .onComplete({
          case rawJson: Success[SimpleHttpResponse] =>
            decode[DocsRes[E]](rawJson.get.body) match {
              case Left(e) =>
                val errMsg = s"unable to decode response from get documents: ${e.getMessage()}"
                p failure CloudFirestoreException(errMsg)
              case Right(docs) =>
                p success docs.documents.sortBy(_.fields.sortingField).reverse
            }
          case f: Failure[SimpleHttpResponse] =>
            val errMsg = s"failed getting documents: ${f.exception.getMessage()}"
            p failure CloudFirestoreException(errMsg)
        })
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
    val p = Promise[Doc[E]]()

    if (isBadRequest(token, entity.content))
      p failure CloudFirestoreException("")
    else
      Future {
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
      }

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
