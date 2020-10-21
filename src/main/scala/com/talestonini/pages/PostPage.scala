package com.talestonini.pages

import com.talestonini.App.user
import com.talestonini.db.Firebase
import com.talestonini.db.model._
import com.talestonini.utils._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

trait PostPage {

  /**
    * A binding comment.
    */
  case class BComment(
    author: Var[String],
    text: Var[String],
    date: Var[String]
  )

  val bComments = Vars.empty[BComment]

  val postRestEntityLinkPromise = Promise[String]()

  postRestEntityLinkPromise.future
    .onComplete({
      case link: Success[String] =>
        Firebase
          .getComments(user.accessToken, link.get)
          .onComplete({
            case comments: Success[Comments] =>
              for (c <- comments.get)
                bComments.value += BComment(
                  author = Var(c.fields.author.get),
                  text = Var(c.fields.text.get),
                  date = Var(datetime2Str(c.fields.date))
                )
            case f: Failure[Comments] =>
              println(s"failure getting comments: ${f.exception.getMessage()}")
          })
      case f: Failure[String] =>
        println(s"failure getting postRestEntityLink: ${f.exception.getMessage()}")
    })

  @html def body() =
    <div>
      <h1>{title()}</h1>

      {content()}

      <h3>Comments ({bComments.length.bind.toString})</h3>
      {comments()}
    </div>

  def title(): String

  def content(): Binding[Node]

  @html def comments() =
    for (c <- bComments)
      yield <p>{c.text.bind}</p>

}
