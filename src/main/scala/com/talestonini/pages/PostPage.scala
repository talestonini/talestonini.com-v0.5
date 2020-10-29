package com.talestonini.pages

import java.time.ZonedDateTime
import java.time.ZoneId

import com.talestonini.App.user
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.utils._
import com.talestonini.utils.observer.{EventName, Observer}
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.BindingSeq
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

trait PostPage extends Observer {

  /**
    * A binding comment.
    */
  private case class BComment(
    author: Var[String],
    text: Var[String],
    date: Var[String]
  )

  // the comments on the page
  private val bComments = Vars.empty[BComment]

  // name of the post document to which this page's comments belong
  private val bPostDocName = Var("")

  // commenting is only allowed if the user is signed in
  var isCommentingAllowed = Var(false)

  val postDocNamePromise = Promise[String]()
  postDocNamePromise.future
    .onComplete({
      case postDocName: Success[String] =>
        bPostDocName.value = postDocName.get
        CloudFirestore
          .getComments(postDocName.get)
          .onComplete({
            case comments: Success[Docs[Comment]] =>
              for (c <- comments.get)
                bComments.value += BComment(
                  author = Var(c.fields.author.get),
                  text = Var(c.fields.text.get),
                  date = Var(datetime2Str(c.fields.date))
                )
            case f: Failure[Docs[Comment]] =>
              println(s"failed getting comments: ${f.exception.getMessage()}")
          })
      case f: Failure[String] =>
        println(s"failed getting post document name: ${f.exception.getMessage()}")
    })

  def title(): String

  def content(): Binding[Node]
  @html def body() =
    <div>
      <h1>{title()}</h1>
      {content()}
      <h3>Comments ({bComments.length.bind.toString})</h3>
      {commentInput()}
      {comments()}
    </div>

  @html def commentInput(): Binding[Node] = {
    val newComment = Var("What do you think?")
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
        persistComment(newComment.value)
        cleanInput()
      }
    }
    val cancelButtonHandler = { e: Event =>
      newComment.value = ""
      cleanInput()
    }
    def display(flag: Boolean): String = if (flag) "block" else "none"
    val signInToComment: Binding[Node] =
      <div style={s"display:${display(!isCommentingAllowed.bind)}"}>
        <p>Please sign in to comment</p>
      </div>
    val commentWidgets: Binding[Node] =
      <div style={s"display:${display(isCommentingAllowed.bind)}"}>
        {inputBinding.bind}
        <button onclick={commentButtonHandler}>Comment</button>
        <button onclick={cancelButtonHandler}>Cancel</button>
      </div>
    val div =
      <div>
        {signInToComment}
        {commentWidgets}
      </div>
    div
  }

  def persistComment(comment: String): Unit = {
    val c = Comment(
      author = Some(user.displayName.value),
      date = Some(ZonedDateTime.now(ZoneId.of("UTC"))),
      text = Some(comment)
    )
    CloudFirestore
      .createComment(user.accessToken, bPostDocName.value, c)
      .onComplete({
        case c: Success[Doc[Comment]] =>
          bComments.value += BComment(
            author = Var(c.value.fields.author.get),
            date = Var(datetime2Str(c.value.fields.date)),
            text = Var(c.value.fields.text.get)
          )
        case f: Failure[Doc[Comment]] =>
          println("failed creating comment")
      })
  }

  @html def comments() =
    for (c <- bComments)
      yield <p>{c.text.bind} ({c.author.bind}, {c.date.bind})</p>

  user.register(this, "UserSignedIn", "UserSignedOut")

  def onNotify(e: EventName): Unit = e match {
    case "UserSignedIn"  => isCommentingAllowed.value = true
    case "UserSignedOut" => isCommentingAllowed.value = false
  }

}
