package com.talestonini.db

import cats.syntax.either._
import com.talestonini.db.model._
import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method.{GET, POST}
import fr.hmil.roshttp.Protocol.HTTPS
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe._
import io.circe.parser._
import monix.execution.Scheduler.Implicits.{global => scheduler}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions._
import scala.util.{Failure, Success}

object Firebase {

  private val ApiKey        = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY"
  private val ProjectId     = "ttdotcom"
  private val Database      = "(default)"
  private val FirestoreHost = "firestore.googleapis.com"
  private val pathPrefix    = "/v1"

  private val commonHeaders = {
    "Access-Control-Allow-Origin"  -> "*"
    "Access-Control-Allow-Headers" -> "Content-Type"
    "Access-Control-Allow-Methods" -> "POST"
    "Content-Type"                 -> "application/json"
  }

  def getAuthToken(): Future[String] = {
    val p = Promise[String]()

    Future {
      HttpRequest()
        .withMethod(POST)
        .withProtocol(HTTPS)
        .withHost("identitytoolkit.googleapis.com")
        .withPath(s"$pathPrefix/accounts:signUp")
        .withQueryParameter("key", Firebase.ApiKey)
        .withHeaders(Firebase.commonHeaders)
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
    get[Post](token, s"/projects/$ProjectId/databases/$Database/documents/posts")

  def getComments(token: String, postRestEntityLink: String): Future[Comments] =
    get[Comment](token, s"$postRestEntityLink/comments")

  private def get[D <: DocType](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[D]]
  ): Future[Docs[D]] = {
    val p = Promise[Docs[D]]()
    Future {
      HttpRequest()
        .withMethod(GET)
        .withProtocol(HTTPS)
        .withHost(FirestoreHost)
        .withPath(pathPrefix + path)
        .withHeader("Authorization", s"Bearer $token")
        .send()
        .onComplete({
          case rawJson: Success[SimpleHttpResponse] =>
            val docs = decode[DocsRes[D]](rawJson.get.body) match {
              case Left(e) =>
                println(s"unable to decode GET docs response: ${e.getMessage()}")
                Seq.empty
              case Right(res) =>
                res.documents
            }
            p success docs
          case f: Failure[SimpleHttpResponse] =>
            println(s"GET comments request failed: ${f.exception.getMessage()}")
            p success Seq.empty
        })
    }
    p.future
  }

}
