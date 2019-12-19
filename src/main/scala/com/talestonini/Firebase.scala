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


object Firebase {

  case class PostFields(title: Option[String],
                        resource: Option[String],
                        firstPublishDate: Option[LocalDateTime],
                        publishDate: Option[LocalDateTime]
  )
  
  case class CommentFields(author: Option[String], date: Option[LocalDateTime], text: Option[String])
  
  case class Doc[D](name: String, fields: D, createTime: String, updateTime: String)
  
  case class DocsRes[D](documents: Seq[Doc[D]])

  type Posts = Seq[Doc[PostFields]]

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

  private val LongDateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private val ShortDateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

  lazy implicit val decodeLocalDateTime: Decoder[LocalDateTime] = 
    Decoder.decodeString.emap { str =>
      // TODO: is a try-catch correct inside catchNonFatal?
      Either.catchNonFatal(
        try {
          LocalDateTime.parse(str, LongDateTimeFormatter)
        } catch {
          case _: Exception => LocalDateTime.parse(str, ShortDateTimeFormatter)
        }
      ).leftMap(t => "LocalDateTime")
    }

  lazy implicit val decodePostFields: Decoder[PostFields] = new Decoder[PostFields] {
    final def apply(c: HCursor): Decoder.Result[PostFields] =
      for {
        title <- c.downField("title").get[String]("stringValue")
        resource <- c.downField("resource").get[String]("stringValue")
        firstPublishDate <- c.downField("first_publish_date").get[LocalDateTime]("timestampValue")
        publishDate <- c.downField("publish_date").get[LocalDateTime]("timestampValue")
      } yield PostFields(Option(title), Option(resource), Option(firstPublishDate), Option(publishDate))
  }

  lazy implicit val decodePostDoc: Decoder[Doc[PostFields]] = new Decoder[Doc[PostFields]] {
    final def apply(c: HCursor): Decoder.Result[Doc[PostFields]] = 
      for {
        name <- c.get[String]("name")
        fields <- c.get[PostFields]("fields")
        createTime <- c.get[String]("createTime")
        updateTime <- c.get[String]("updateTime")
      } yield Doc(name, fields, createTime, updateTime)
  }

  lazy implicit val decodePosts: Decoder[DocsRes[PostFields]] = new Decoder[DocsRes[PostFields]] {
    final def apply(c: HCursor): Decoder.Result[DocsRes[PostFields]] = 
      for {
        docs <- c.get[Seq[Doc[PostFields]]]("documents")
      } yield DocsRes(docs)
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

  def getPosts(token: String): Future[Posts] = {
    val p = Promise[Posts]()
    Future {
      HttpRequest()
        .withMethod(GET)
        .withProtocol(HTTPS)
        .withHost(FirestoreHost)
        .withPath(s"/v1/projects/$ProjectId/databases/$Database/documents/posts")
        .withHeader("Authorization", s"Bearer $token")
        .send()
        .onComplete({
          case rawJson: Success[SimpleHttpResponse] =>
            val posts = decode[DocsRes[PostFields]](rawJson.get.body) match {
              case Left(e) =>
                println(s"unable to decode GET posts response: ${e.getMessage()}")
                Seq.empty
              case Right(res) =>
                res.documents
            }
            p success posts
          case f: Failure[SimpleHttpResponse] => 
            println(s"GET posts request failed: ${f.exception.getMessage()}")
            p success Seq.empty
        })
    }
    p.future
  }

}

