package com.talestonini.db

import java.time._
import java.time.format.DateTimeFormatter.ofPattern

import cats.effect.IO
import com.talestonini.utils._
import io.circe._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.EntityDecoder
import org.http4s.circe.CirceEntityEncoder
import org.http4s.EntityEncoder
import scala.language.implicitConversions

package object model {

  private val LongDateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  implicit lazy val decodeZonedDateTime: Decoder[ZonedDateTime] = Decoder.decodeZonedDateTime

  // --- common --------------------------------------------------------------------------------------------------------

  // signUp response (get auth token)
  case class AuthTokenResponse(kind: String, idToken: String, refreshToken: String, expiresIn: String, localId: String)

  // follows Cloud Firestore specs
  case class Doc[M](name: String, fields: M, createTime: String, updateTime: String)

  type Docs[M] = Seq[Doc[M]]

  case class DocsRes[M](documents: Docs[M])

  sealed trait Model {
    def dbFields: Seq[String]
    def content: String
    def sortingField: String
  }

  case class Body[M](name: String, fields: M)

  implicit def bodyEncoder[M <: Model](implicit modelEncoder: Encoder[M]): Encoder[Body[M]] =
    new Encoder[Body[M]] {
      def apply(body: Body[M]): Json =
        Json.obj(
          "name"   -> Json.fromString(body.name),
          "fields" -> modelEncoder(body.fields)
        )
    }

  // implicit def bodyEntityEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[IO, A] = jsonEncoderOf[IO, A]

  implicit def docDecoder[M <: Model](implicit fieldsDecoder: Decoder[M]): Decoder[Doc[M]] =
    // name, fields, createTime and updateTime are part of Cloud Firestore specs
    new Decoder[Doc[M]] {
      final def apply(c: HCursor): Decoder.Result[Doc[M]] =
        for {
          name       <- c.get[String]("name")
          fields     <- c.get[M]("fields")
          createTime <- c.get[String]("createTime")
          updateTime <- c.get[String]("updateTime")
        } yield Doc(name, fields, createTime, updateTime)
    }

  implicit def docsResDecoder[M <: Model](implicit docsDecoder: Decoder[Docs[M]]): Decoder[DocsRes[M]] =
    new Decoder[DocsRes[M]] {
      final def apply(c: HCursor): Decoder.Result[DocsRes[M]] =
        for {
          docs <- c.get[Docs[M]]("documents")
        } yield DocsRes(docs)
    }

  implicit def responseEntityDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[IO, A] = jsonOf[IO, A]

  // --- post (ie article) ---------------------------------------------------------------------------------------------

  case class Post(
    resource: Option[String], title: Option[String], firstPublishDate: Option[ZonedDateTime],
    publishDate: Option[ZonedDateTime], enabled: Option[Boolean] = Some(true)
  ) extends Model {
    def dbFields: Seq[String] = Seq("resource", "title", "first_publish_date", "publish_date", "enabled")
    def content: String       = title.getOrElse("")
    def sortingField: String  = datetime2Str(publishDate.getOrElse(InitDateTime), DateTimeCompareFormatter)
  }

  implicit lazy val postEncoder: Encoder[Post] =
    new Encoder[Post] {
      final def apply(p: Post): Json = {
        Json.obj(
          Seq[Option[(String, Json)]](
            p.resource.map(r => "resource" -> field("stringValue", r)),
            p.title.map(t => "title" -> field("stringValue", t)),
            p.firstPublishDate.map(fpd =>
              "first_publish_date" -> field("timestampValue", fpd.format(LongDateTimeFormatter))),
            p.publishDate.map(pd => "publish_date" -> field("timestampValue", pd.format(LongDateTimeFormatter))),
            p.enabled.map(e => "enabled" -> field("booleanValue", e))
          ).filter(_.isDefined).map(_.get): _*
        )
      }
    }

  implicit lazy val postDecoder: Decoder[Post] =
    // title, resource, first_publish_date and publish_date are my database specs
    new Decoder[Post] {
      final def apply(c: HCursor): Decoder.Result[Post] =
        for {
          resource         <- c.downField("resource").get[String]("stringValue")
          title            <- c.downField("title").get[String]("stringValue")
          firstPublishDate <- c.downField("first_publish_date").getOrElse[ZonedDateTime]("timestampValue")(InitDateTime)
          publishDate      <- c.downField("publish_date").getOrElse[ZonedDateTime]("timestampValue")(InitDateTime)
          enabled          <- c.downField("enabled").getOrElse[Boolean]("booleanValue")(true)
        } yield Post(Option(resource), Option(title), Option(firstPublishDate), Option(publishDate), Option(enabled))
    }

  // --- comment -------------------------------------------------------------------------------------------------------

  case class Comment(
    author: Option[User], date: Option[ZonedDateTime], text: Option[String]
  ) extends Model {
    def dbFields: Seq[String] = Seq("author", "date", "text")
    def content: String       = text.getOrElse("")
    def sortingField: String  = datetime2Str(date.getOrElse(InitDateTime), DateTimeCompareFormatter)
  }

  implicit lazy val commentEncoder: Encoder[Comment] =
    new Encoder[Comment] {
      final def apply(c: Comment): Json = {
        Json.obj(
          Seq[Option[(String, Json)]](
            c.author.map(a => "author" -> field("mapValue", Json.obj(("fields", a)))),
            c.date.map(d => "date" -> field("timestampValue", d.format(LongDateTimeFormatter))),
            c.text.map(t => "text" -> field("stringValue", t))
          ).filter(_.isDefined).map(_.get): _*
        )
      }
    }

  implicit lazy val commentDecoder: Decoder[Comment] =
    // author, date and text are my database specs
    new Decoder[Comment] {
      final def apply(c: HCursor): Decoder.Result[Comment] =
        for {
          author <- c.downField("author").downField("mapValue").get[User]("fields")
          date   <- c.downField("date").get[ZonedDateTime]("timestampValue")
          text   <- c.downField("text").get[String]("stringValue")
        } yield Comment(Option(author), Option(date), Option(text))
    }

  // --- user ----------------------------------------------------------------------------------------------------------

  case class User(
    name: Option[String], email: Option[String], uid: Option[String]
  ) extends Model {
    def dbFields: Seq[String] = Seq("name", "email", "uid")
    def content: String       = name.getOrElse("")
    def sortingField: String  = email.getOrElse("")
  }

  implicit lazy val userEncoder: Encoder[User] =
    new Encoder[User] {
      final def apply(u: User): Json = {
        Json.obj(
          Seq[Option[(String, Json)]](
            u.name.map(n => "name" -> field("stringValue", n)),
            u.email.map(e => "email" -> field("stringValue", e)),
            u.uid.map(uid => "uid" -> field("stringValue", uid))
          ).filter(_.isDefined).map(_.get): _*
        )
      }
    }

  implicit def userAsJson(user: User): Json = userEncoder(user)

  implicit lazy val userDecoder: Decoder[User] =
    new Decoder[User] {
      def apply(c: HCursor): Decoder.Result[User] =
        for {
          name  <- c.downField("name").get[String]("stringValue")
          email <- c.downField("email").get[String]("stringValue")
          uid   <- c.downField("uid").get[String]("stringValue")
        } yield User(Option(name), Option(email), Option(uid))
    }

  // -------------------------------------------------------------------------------------------------------------------

  private def field(`type`: String, value: String): Json =
    Json.fromJsonObject(JsonObject((`type`, Json.fromString(value))))
  private def field(`type`: String, value: Boolean): Json =
    Json.fromJsonObject(JsonObject((`type`, Json.fromBoolean(value))))
  private def field(`type`: String, value: Json): Json =
    Json.fromJsonObject(JsonObject((`type`, value)))

}
