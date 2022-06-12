package com.talestonini.db

import com.talestonini.db.model._
import io.circe.{Encoder, Decoder}
import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._

trait Database[M[_]] {

  def getAuthTokenF(): M[String]

  def getDocumentsF[T <: Model](token: String, path: String)(
    implicit docsResDecoder: Decoder[DocsRes[T]]
  ): M[Docs[T]]

  def upsertDocumentF[T <: Model](token: String, path: String, model: T)(
    implicit docDecoder: Decoder[Doc[T]], bodyEncoder: Encoder[Body[T]]
  ): M[Doc[T]]

  def deleteDocumentF[T <: Model](token: String, path: String)(
    implicit docDecoder: Decoder[Doc[T]]
  ): M[Option[Throwable]]

  def getDocumentsF[T <: Model, M[_]: Monad](db: Database[M], path: String)(
    implicit docsResDecoder: Decoder[DocsRes[T]]
  ): M[Docs[T]] =
    for {
      token <- db.getAuthTokenF()
      docs  <- db.getDocumentsF(token, path)
    } yield docs

  def upsertDocumentF[T <: Model, M[_]: Monad](db: Database[M], path: String, model: T)(
    implicit docDecoder: Decoder[Doc[T]], bodyEncoder: Encoder[Body[T]]
  ): M[Doc[T]] =
    for {
      token <- db.getAuthTokenF()
      doc   <- db.upsertDocumentF(token, path, model)
    } yield doc

  def deleteDocumentF[T <: Model, M[_]: Monad](db: Database[M], path: String)(
    implicit docDecoder: Decoder[Doc[T]]
  ): M[Option[Throwable]] =
    for {
      token <- db.getAuthTokenF()
      t     <- db.deleteDocumentF(token, path)
    } yield t

}
