package com.talestonini

import java.time._
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.response.SimpleHttpResponse
import fr.hmil.roshttp.Protocol.HTTPS
import fr.hmil.roshttp.Method.{GET, POST}
import fr.hmil.roshttp.body.PlainTextBody
import monix.execution.Scheduler.Implicits.{global => scheduler}
import io.circe._, io.circe.parser._, io.circe.generic.semiauto._, io.circe.parser.decode
import cats.syntax.either._


case class Comment(author: Option[String], date: Option[LocalDate], text: Option[String])

case class Post(title: Option[String],
                resource: Option[String],
                firstPublishDate: Option[LocalDate],
                publishDate: Option[LocalDate],
)

object Firebase {

  lazy implicit val decodeLocalDate: Decoder[LocalDate] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(LocalDate.parse(str, ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))).leftMap(t => "LocalDate")
  }

  lazy implicit val decodePost: Decoder[Post] = new Decoder[Post] {
    final def apply(c: HCursor): Decoder.Result[Post] =
      for {
        title <- c.downField("title").get[String]("stringValue")
        resource <- c.downField("resource").get[String]("stringValue")
        firstPublishDate <- c.downField("first_publish_date").get[LocalDate]("timestampValue")
        publishDate <- c.downField("publish_date").get[LocalDate]("timestampValue")
      } yield Post(Option(title), Option(resource), Option(firstPublishDate), Option(publishDate))
  }

  val ApiKey = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY"
  val ProjectId = "ttdotcom"
  val Database = "(default)"
  val FirestoreHost = "firestore.googleapis.com"

  val commonHeaders = {
    "Access-Control-Allow-Origin" -> "*"
    "Access-Control-Allow-Headers" -> "Content-Type"
    "Access-Control-Allow-Methods" -> "POST"
    "Content-Type" -> "application/json"
  }

  def getAuthToken(): Future[String] = {
    val p = Promise[String]()
    Future {
      HttpRequest()
        .withMethod(POST)
        .withProtocol(HTTPS)
        .withHost("identitytoolkit.googleapis.com")
        .withPath("/v1/accounts:signUp")
        .withQueryParameter("key", Firebase.ApiKey)
        .withHeaders(Firebase.commonHeaders)
        .send()
        .onComplete({
          case rawJson: Success[SimpleHttpResponse] => 
            val json = parse(rawJson.get.body).getOrElse(Json.Null)
            p success (json \\ "idToken")(0).asString.get
          case e: Failure[SimpleHttpResponse] => 
            p success "unable to obtain token"
        })
    }
    p.future
  }

  def getPosts(token: String): Future[Array[Post]] = {
    val p = Promise[Array[Post]]()
    Future {
      HttpRequest()
        .withMethod(GET)
        .withProtocol(HTTPS)
        .withHost(FirestoreHost)
        .withPath(s"/v1/projects/$ProjectId/databases/$Database/documents/posts")
        .withHeaders(Firebase.commonHeaders)
        .withHeader("Authorization", s"Bearer $token")
        .send()
        .onComplete(
        {
          case rawJson: Success[SimpleHttpResponse] => 
            //parse(rawJson.get.body) match {
              //case Left(failure) =>
                //println("invalid JSON response from GET posts")
              //case Right(json) =>
                //println("navigating JSON response from GET posts")
                //println(">>> " + (json \\ "documents")(0))
                //val docs = (json \\ "documents")
                //for (d <- docs) {
                  //println(">>> " + d)
                //}
            //}
            decode[List[Post]](rawJson.get.body)
            p success Array.empty[Post]
          case e: Failure[SimpleHttpResponse] => 
            p success Array.empty[Post]
        }
        )
    }
    p.future
  }

}

