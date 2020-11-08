package com.talestonini.pages

import java.time.ZonedDateTime
import java.time.ZoneId

import com.talestonini.App.user
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.utils._
import com.talestonini.utils.js.display
import com.talestonini.utils.observer.EventName._
import com.talestonini.utils.observer.Observer
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

trait PostPage extends Observer {

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

  // when the promise for the name of post document to which this page's comments belong fulfills,
  // retrieve the comments from db
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
                  author = Var(c.fields.author.get.name.get),
                  text = Var(c.fields.text.get),
                  date = Var(datetime2Str(c.fields.date))
                )
            case f: Failure[Docs[Comment]] =>
              println(s"failed getting comments: ${f.exception.getMessage()}")
          })
      case f: Failure[String] =>
        println(s"failed getting post document name: ${f.exception.getMessage()}")
    })

  // observe the user sign in/out to allow or not commenting on the post
  user.register(this, UserSignedIn, UserSignedOut)
  def onNotify(e: EventName): Unit = e match {
    case UserSignedIn  => isAllowedToComment.value = true
    case UserSignedOut => isAllowedToComment.value = false
  }

  // -------------------------------------------------------------------------------------------------------------------

  // a binding comment
  private case class BComment(
    author: Var[String],
    text: Var[String],
    date: Var[String]
  )

  // the comments on this page
  private val bComments = Vars.empty[BComment]

  @html private def comments() =
    for (c <- bComments)
      yield <p>{c.text.bind} ({c.author.bind}, {c.date.bind})</p>

  // name of the post document to which this page's comments belong
  private val bPostDocName = Var("")

  // commenting is only allowed if the user is signed in
  private var isAllowedToComment = Var(false)

  // widget for inputting a new commment
  @html private def commentInput(): Binding[Node] = {
    val initComment = "What do you think?"
    val bText       = Var(initComment)

    val bTextArea: NodeBinding[HTMLTextAreaElement] =
      <textarea rows="5" value={bText.bind} onfocus={e: Event => bText.value = ""} />

    def cleanTextArea() = bText.value = initComment

    val commentButtonHandler = { e: Event =>
      val textArea = bTextArea.value
      if (textArea.value != "" && textArea.value != initComment) {
        bText.value = textArea.value
        persistComment(bText.value)
        cleanTextArea()
      }
    }

    val cancelButtonHandler = { e: Event => cleanTextArea() }

    val signInToComment: Binding[Node] =
      <div style={s"display:${display(!isAllowedToComment.bind)}"}>
        <p>Please sign in to comment</p>
      </div>

    val commentWidgets: Binding[Node] =
      <div style={s"display:${display(isAllowedToComment.bind)}"}>
        {bTextArea.bind}
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

  // persist new comment into db
  private val Now = ZonedDateTime.now(ZoneId.of("UTC"))
  private def persistComment(comment: String): Unit = {
    val dbUser = com.talestonini.db.model.User(
      name = Option(user.displayName.value),
      email = Option(user.email.value),
      uid = Option(user.uid.value)
    )
    val c = Comment(
      author = Option(dbUser),
      date = Option(Now),
      text = Option(comment)
    )
    CloudFirestore
      .createComment(user.accessToken, bPostDocName.value, c)
      .onComplete({
        case doc: Success[Doc[Comment]] =>
          bComments.value += BComment(
            author = Var(doc.value.fields.author.get.name.get),
            date = Var(datetime2Str(doc.value.fields.date)),
            text = Var(doc.value.fields.text.get)
          )
        case f: Failure[Doc[Comment]] =>
          println("failed creating comment")
      })
  }

}
