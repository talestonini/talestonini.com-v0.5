package com.talestonini.pages

object CodeSnippets {

  object DockerVim {

    def codeSnippet1() = """private def getDocuments[E <: Entity](token: String, path: String)(
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
"""
  }

}
