package com.talestonini.db

import cats.syntax.either._
import com.talestonini.db.model._
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

object Firebase {

  private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY"
  private val ProjectId     = "ttdotcom"
  private val Database      = "(default)"
  private val FirestoreHost = "firestore.googleapis.com"

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
            val token = json.hcursor.get[String]("idToken") match {
              case Left(e) =>
                println(s"unable to decode POST signUp response: ${e.getMessage()}")
                "no token"
              case Right(res) =>
                res
            }
            p success token
          case f: Failure[SimpleHttpResponse] =>
            println(s"POST signUp request failed: ${f.exception.getMessage()}")
            p success "no token"
        })
    }
    p.future
  }

  def getPosts(token: String): Future[Posts] =
    get[Post](token, s"projects/$ProjectId/databases/$Database/documents/posts")

  def getPosts(): Future[Posts] =
    get[Post](s"projects/$ProjectId/databases/$Database/documents/posts")

  def getComments(token: String, postRestEntityLink: String): Future[Comments] =
    get[Comment](token, s"$postRestEntityLink/comments")

  def getComments(postRestEntityLink: String): Future[Comments] =
    get[Comment](s"$postRestEntityLink/comments")

  def postComment(token: String, postRestEntityLink: String, comment: Comment) =
    post[Comment](token, postRestEntityLink, comment)

  // -------------------------------------------------------------------------------------------------------------------

  private def get[D <: DocType](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[D]]
  ): Future[Docs[D]] = {
    val dType = docType(path)
    val p     = Promise[Docs[D]]()
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
            val docs = decode[DocsRes[D]](rawJson.get.body) match {
              case Left(e) =>
                println(s"unable to decode GET $dType response: ${e.getMessage()}")
                Seq.empty
              case Right(res) =>
                println(s"successfuly retrieved $dType")
                res.documents
            }
            p success docs
          case f: Failure[SimpleHttpResponse] =>
            println(s"GET $dType request failed: ${f.exception.getMessage()}")
            p success Seq.empty
        })
    }
    p.future
  }

  private def get[D <: DocType](path: String)(
    implicit docsResDecoder: Decoder[DocsRes[D]]
  ): Future[Docs[D]] = {
    val p = Promise[Docs[D]]()
    getAuthToken()
      .onComplete({
        case token: Success[String] =>
          p completeWith get[D](token.get, path)
        case f: Failure[String] =>
          println(s"failure getting auth token: ${f.exception.getMessage()}")
          p success Seq.empty
      })
    p.future
  }

  private def post[D <: DocType](token: String, path: String, body: D)(
    implicit docsResDecoder: Decoder[DocsRes[D]]
  ): Future[Docs[D]] = {
    val dType = docType(path)
    val p     = Promise[Docs[D]]()
    val resId = randomAlphaNumericString(20)
    Future {
      HttpRequest()
        .withMethod(PATCH)
        .withProtocol(HTTPS)
        .withHost(FirestoreHost)
        .withPath(s"/v1/$path/comments/$resId")
        .withQueryParameters(
            Seq(
              ("updateMask.fieldPaths", "author"),
              ("updateMask.fieldPaths", "date"),
              ("updateMask.fieldPaths", "text")
            ): _*)
        .withHeader("Authorization", s"Bearer $token")
        .withBody(comment2Body(path, body.asInstanceOf[Comment], resId))
        .send()
        .onComplete({
          case rawJson: Success[SimpleHttpResponse] =>
            val docs = decode[DocsRes[D]](rawJson.get.body) match {
              case Left(e) =>
                println(s"unable to decode POST $dType response: ${e.getMessage()}")
                Seq.empty
              case Right(res) =>
                println(s"successfuly created $dType")
                res.documents
            }
            p success docs
          case f: Failure[SimpleHttpResponse] =>
            println(s"POST $dType request failed: ${f.exception.getMessage()}")
            p success Seq.empty
        })
    }
    p.future
  }

  private def docType(path: String) = {
    val idx = path.lastIndexOf("/")
    path.substring(idx + 1)
  }

  private def randomAlphaNumericString(length: Int): String = {
    def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
      val sb = new StringBuilder
      for (i <- 1 to length) {
        val randomNum = util.Random.nextInt(chars.length)
        sb.append(chars(randomNum))
      }
      sb.toString
    }

    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    randomStringFromCharList(length, chars)
  }

}
