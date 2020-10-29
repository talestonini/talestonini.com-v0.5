package com.talestonini.pages

import java.time.ZonedDateTime

import com.talestonini.App.user
import com.talestonini.db.Firebase
import com.talestonini.db.model._
import com.talestonini.utils._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.HTMLTextAreaElement
import org.scalajs.dom.raw.Node
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}
import java.time.ZoneId

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
  val postRestEntityLink        = Var("")

  val newComments = Var("What do you think?")

  postRestEntityLinkPromise.future
    .onComplete({
      case link: Success[String] =>
        postRestEntityLink.value = link.get
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
      {commentInput(newComments)}
      {comments()}
    </div>

  def title(): String

  def content(): Binding[Node]

  @html def commentInput(newComment: Var[String]): Binding[Node] = {
    val inputBinding: NodeBinding[HTMLTextAreaElement] =
      <textarea rows="5" value={newComment.bind} onfocus={e: Event => newComment.value = ""} />
    def cleanInput() = {
      val input = inputBinding.value
      input.value = ""
    }
    val commentButtonHandler = { e: Event =>
      val input = inputBinding.value
      if (input.value != "") {
        newComment.value = input.value

        val c = Comment(
          author = Some(user.displayName.value),
          date = Some(ZonedDateTime.now(ZoneId.of("UTC"))),
          text = Some(newComments.value)
        )
        Firebase.postComment(user.accessToken, postRestEntityLink.value, c)

        cleanInput()
      }
    }
    val cancelButtonHandler = { e: Event =>
      newComments.value = ""
      cleanInput()
    }
    <div>
      {inputBinding.bind}
      <button onclick={commentButtonHandler}>Comment</button>
      <button onclick={cancelButtonHandler}>Cancel</button>
    </div>
  }

  @html def comments() =
    for (c <- bComments)
      yield <p>{c.text.bind} ({c.author.bind}, {c.date.bind})</p>

}
