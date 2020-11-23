package com.talestonini.db

import java.time._
import java.time.format.DateTimeFormatter.ofPattern

import com.talestonini.utils._
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.body.JSONBody.{JSONObject, JSONString, JSONValue}
import io.circe._

package object model {

  private val LongDateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  implicit lazy val decodeZonedDateTime: Decoder[ZonedDateTime] = Decoder.decodeZonedDateTime

  // --- common --------------------------------------------------------------------------------------------------------

  // follows Cloud Firestore specs
  case class Doc[E](name: String, fields: E, createTime: String, updateTime: String)

  case class DocsRes[E](documents: Seq[Doc[E]])

  sealed trait Entity {
    def dbFields: Seq[String]
    def content: String
    def sortingField: String
  }

  type Docs[E] = Seq[Doc[E]]

  implicit def docDecoder[E <: Entity](implicit fieldsDecoder: Decoder[E]): Decoder[Doc[E]] =
    // name, fields, createTime and updateTime are part of Cloud Firestore specs
    new Decoder[Doc[E]] {
      final def apply(c: HCursor): Decoder.Result[Doc[E]] =
        for {
          name       <- c.get[String]("name")
          fields     <- c.get[E]("fields")
          createTime <- c.get[String]("createTime")
          updateTime <- c.get[String]("updateTime")
        } yield Doc(name, fields, createTime, updateTime)
    }

  implicit def docsResDecoder[E <: Entity](implicit docSeqDecoder: Decoder[Seq[Doc[E]]]): Decoder[DocsRes[E]] =
    new Decoder[DocsRes[E]] {
      final def apply(c: HCursor): Decoder.Result[DocsRes[E]] =
        for {
          docs <- c.get[Seq[Doc[E]]]("documents")
        } yield DocsRes(docs)
    }

  def entityToDocBody[E <: Entity](name: String, entity: E): JSONObject =
    JSONObject(
      "name" -> name,
      "fields" -> (entity match {
        // type match is the easiest for now (not keen on reflection)
        case p: Post =>
          JSONObject(
            Seq[Option[(String, JSONValue)]](
              p.title.map(t => "title"       -> field("stringValue", t)),
              p.resource.map(r => "resource" -> field("stringValue", r)),
              p.firstPublishDate.map(fpd =>
                "first_publish_date" -> field("timestampValue", fpd.format(LongDateTimeFormatter))
              ),
              p.publishDate.map(pd => "publish_date" -> field("timestampValue", pd.format(LongDateTimeFormatter)))
            ).filter(_.isDefined).map(_.get): _*
          )
        case c: Comment =>
          JSONObject(
            Seq[Option[(String, JSONValue)]](
              c.author.map(a => "author" -> field("mapValue", userToJsonValue(a))),
              c.date.map(d => "date"     -> field("timestampValue", d.format(LongDateTimeFormatter))),
              c.text.map(t => "text"     -> field("stringValue", t))
            ).filter(_.isDefined).map(_.get): _*
          )
        case _ =>
          throw new Exception(s"unexpected entity type: ${entity.getClass()}")
      })
    )

  // --- post (ie article) ---------------------------------------------------------------------------------------------

  case class Post(
    title: Option[String],
    firstPublishDate: Option[ZonedDateTime],
    publishDate: Option[ZonedDateTime],
    resource: Option[String]
  ) extends Entity {
    def dbFields: Seq[String] = Seq("title", "resource", "first_publish_date", "publish_date")
    def content: String       = title.getOrElse("")
    def sortingField: String  = datetime2Str(publishDate.getOrElse(InitDateTime), DateTimeCompareFormatter)
  }

  implicit lazy val postFieldsDecoder: Decoder[Post] =
    // title, resource, first_publish_date and publish_date are my database specs
    new Decoder[Post] {
      final def apply(c: HCursor): Decoder.Result[Post] =
        for {
          title            <- c.downField("title").get[String]("stringValue")
          firstPublishDate <- c.downField("first_publish_date").get[ZonedDateTime]("timestampValue")
          publishDate      <- c.downField("publish_date").get[ZonedDateTime]("timestampValue")
          resource         <- c.downField("resource").get[String]("stringValue")
        } yield Post(Option(title), Option(firstPublishDate), Option(publishDate), Option(resource))
    }

  // --- comment -------------------------------------------------------------------------------------------------------

  case class Comment(
    author: Option[User],
    date: Option[ZonedDateTime],
    text: Option[String]
  ) extends Entity {
    def dbFields: Seq[String] = Seq("author", "date", "text")
    def content: String       = text.getOrElse("")
    def sortingField: String  = datetime2Str(date.getOrElse(InitDateTime), DateTimeCompareFormatter)
  }

  implicit lazy val commentFieldsDecoder: Decoder[Comment] =
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
    name: Option[String],
    email: Option[String],
    uid: Option[String]
  ) extends Entity {
    def dbFields: Seq[String] = Seq("name", "email", "uid")
    def content: String       = name.getOrElse("")
    def sortingField: String  = email.getOrElse("")
  }

  implicit lazy val userFieldsDecoder: Decoder[User] =
    new Decoder[User] {
      def apply(c: HCursor): Decoder.Result[User] =
        for {
          name  <- c.downField("name").get[String]("stringValue")
          email <- c.downField("email").get[String]("stringValue")
          uid   <- c.downField("uid").get[String]("stringValue")
        } yield User(Option(name), Option(email), Option(uid))
    }

  private def userToJsonValue(user: User): JSONValue =
    JSONObject(
      "fields" -> JSONObject(
        Seq[Option[(String, JSONValue)]](
          user.name.map(n => "name"   -> field("stringValue", n)),
          user.email.map(e => "email" -> field("stringValue", e)),
          user.uid.map(u => "uid"     -> field("stringValue", u))
        ).filter(_.isDefined).map(_.get): _*
      )
    )

  // -------------------------------------------------------------------------------------------------------------------

  private def field(`type`: String, value: String): JSONObject    = JSONObject(`type` -> new JSONString(value))
  private def field(`type`: String, value: JSONValue): JSONObject = JSONObject(`type` -> value)

}
