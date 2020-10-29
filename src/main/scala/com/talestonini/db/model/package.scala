package com.talestonini.db

import java.time._
import java.time.format.DateTimeFormatter.ofPattern

import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.body.JSONBody.{JSONObject, JSONString, JSONValue}
import io.circe._

package object model {

  private val LongDateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  implicit lazy val decodeZonedDateTime: Decoder[ZonedDateTime] = Decoder.decodeZonedDateTime

  def randomAlphaNumericString(length: Int): String = {
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

  // --- common --------------------------------------------------------------------------------------------------------

  // follows Cloud Firestore specs
  case class Doc[E](name: String, fields: E, createTime: String, updateTime: String)

  case class DocsRes[E](documents: Seq[Doc[E]])

  sealed trait Entity {
    def dbFields: Seq[String]
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

  def entityToDocBody[E <: Entity](name: String, entity: E): JSONObject = {
    def field(`type`: String, value: String): JSONObject = JSONObject(`type` -> new JSONString(value))

    JSONObject(
      "name" -> name,
      "fields" -> (entity match {
        // type match is the easiest for now (not keen on reflection)
        case c: Comment =>
          JSONObject(
            Seq[Option[(String, JSONValue)]](
              c.author.map(a => "author" -> field("stringValue", a)),
              c.date.map(d => "date"     -> field("timestampValue", d.format(LongDateTimeFormatter))),
              c.text.map(t => "text"     -> field("stringValue", t))
            ).filter(_.isDefined).map(_.get): _*
          )
        case _ =>
          throw new Exception(s"unexpected entity type: ${entity.getClass()}")
      })
    )
  }

  // --- post (ie article) ---------------------------------------------------------------------------------------------

  case class Post(
    title: Option[String],
    resource: Option[String],
    firstPublishDate: Option[ZonedDateTime],
    publishDate: Option[ZonedDateTime]
  ) extends Entity {
    def dbFields: Seq[String] = Seq("title", "resource", "first_publish_date", "publish_date")
  }

  implicit lazy val postFieldsDecoder: Decoder[Post] =
    // title, resource, first_publish_date and publish_date are my database specs
    new Decoder[Post] {
      final def apply(c: HCursor): Decoder.Result[Post] =
        for {
          title            <- c.downField("title").get[String]("stringValue")
          resource         <- c.downField("resource").get[String]("stringValue")
          firstPublishDate <- c.downField("first_publish_date").get[ZonedDateTime]("timestampValue")
          publishDate      <- c.downField("publish_date").get[ZonedDateTime]("timestampValue")
        } yield Post(Option(title), Option(resource), Option(firstPublishDate), Option(publishDate))
    }

  // --- comment -------------------------------------------------------------------------------------------------------

  case class Comment(
    author: Option[String],
    date: Option[ZonedDateTime],
    text: Option[String]
  ) extends Entity {
    def dbFields: Seq[String] = Seq("author", "date", "text")
  }

  implicit lazy val commentFieldsDecoder: Decoder[Comment] =
    // author, date and text are my database specs
    new Decoder[Comment] {
      final def apply(c: HCursor): Decoder.Result[Comment] =
        for {
          author <- c.downField("author").get[String]("stringValue")
          date   <- c.downField("date").get[ZonedDateTime]("timestampValue")
          text   <- c.downField("text").get[String]("stringValue")
        } yield Comment(Option(author), Option(date), Option(text))
    }

}
