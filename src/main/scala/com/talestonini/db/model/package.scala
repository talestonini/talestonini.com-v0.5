package com.talestonini.db

import java.time._
import java.time.format.DateTimeFormatter.{ofPattern => pattern}

import cats.syntax.either._
import io.circe._

package object model {

  private val LongDateTimeFormatter  = pattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private val ShortDateTimeFormatter = pattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit lazy val decodeLocalDateTime: Decoder[LocalDateTime] =
    Decoder.decodeString.emap { str =>
      // TODO: is a try-catch correct inside catchNonFatal?
      Either
        .catchNonFatal(
          try {
            LocalDateTime.parse(str, LongDateTimeFormatter)
          } catch {
            case _: Exception => LocalDateTime.parse(str, ShortDateTimeFormatter)
          }
        )
        .leftMap(t => "LocalDateTime")
    }

  // --- common --------------------------------------------------------------------------------------------------------

  case class Doc[D](name: String, fields: D, createTime: String, updateTime: String)

  case class DocsRes[D](documents: Seq[Doc[D]])

  sealed trait DocType

  type Docs[D] = Seq[Doc[D]]

  implicit def docDecoder[D <: DocType](implicit fieldsDecoder: Decoder[D]): Decoder[Doc[D]] =
    new Decoder[Doc[D]] {
      final def apply(c: HCursor): Decoder.Result[Doc[D]] =
        for {
          name       <- c.get[String]("name")
          fields     <- c.get[D]("fields")
          createTime <- c.get[String]("createTime")
          updateTime <- c.get[String]("updateTime")
        } yield Doc(name, fields, createTime, updateTime)
    }

  implicit def docsResDecoder[D <: DocType](implicit docSeqDecoder: Decoder[Seq[Doc[D]]]): Decoder[DocsRes[D]] =
    new Decoder[DocsRes[D]] {
      final def apply(c: HCursor): Decoder.Result[DocsRes[D]] =
        for {
          docs <- c.get[Seq[Doc[D]]]("documents")
        } yield DocsRes(docs)
    }

  // --- post (ie article) ---------------------------------------------------------------------------------------------

  case class Post(
    title: Option[String],
    resource: Option[String],
    firstPublishDate: Option[LocalDateTime],
    publishDate: Option[LocalDateTime]
  ) extends DocType

  type Posts = Seq[Doc[Post]]

  implicit lazy val postFieldsDecoder: Decoder[Post] =
    new Decoder[Post] {
      final def apply(c: HCursor): Decoder.Result[Post] =
        for {
          title            <- c.downField("title").get[String]("stringValue")
          resource         <- c.downField("resource").get[String]("stringValue")
          firstPublishDate <- c.downField("first_publish_date").get[LocalDateTime]("timestampValue")
          publishDate      <- c.downField("publish_date").get[LocalDateTime]("timestampValue")
        } yield Post(Option(title), Option(resource), Option(firstPublishDate), Option(publishDate))
    }

  // --- comment -------------------------------------------------------------------------------------------------------

  case class Comment(
    author: Option[String],
    date: Option[LocalDateTime],
    text: Option[String]
  ) extends DocType

  type Comments = Seq[Doc[Comment]]

  implicit lazy val commentFieldsDecoder: Decoder[Comment] =
    new Decoder[Comment] {
      final def apply(c: HCursor): Decoder.Result[Comment] =
        for {
          author <- c.downField("author").get[String]("stringValue")
          date   <- c.downField("date").get[LocalDateTime]("timestampValue")
          text   <- c.downField("text").get[String]("stringValue")
        } yield Comment(Option(author), Option(date), Option(text))
    }

}
