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

trait BasePostPage extends Observer {

  // promise for the post document backing this page
  val postDocPromise = Promise[Doc[Post]]()

  def content(): Binding[Node]

  @html def body() =
    <div>
      <div class="post-title">{bPostDoc.bind.fields.title.getOrElse("")}</div>
      <div class="post-date">{bPostDoc.bind.fields.publishDate.map(pd => datetime2Str(pd)).getOrElse("")}</div>
      <div class="post-content">{content()}</div>
      <div class="post-comments">
        Comments ({bComments.length.bind.toString})
        {commentInput()}
        {comments()}
      </div>
    </div>

  // when the promise for the post document which this page's comments belong to fulfills,
  // retrieve the comments from db
  postDocPromise.future
    .onComplete({
      case postDoc: Success[Doc[Post]] =>
        bPostDoc.value = postDoc.get
        CloudFirestore
          .getComments(postDoc.get.name)
          .onComplete({
            case comments: Success[Docs[Comment]] =>
              for (c <- comments.get)
                bComments.value += BComment(
                  author = Var(c.fields.author.get.name.get),
                  date = Var(datetime2Str(c.fields.date)),
                  text = Var(c.fields.text.get)
                )
            case f: Failure[Docs[Comment]] =>
              println(s"failed getting comments: ${f.exception.getMessage()}")
          })
      case f: Failure[Doc[Post]] =>
        println(s"failed getting post document name: ${f.exception.getMessage()}")
    })

  // observe the user sign in/out to allow or not commenting on the post
  user.register(this, UserSignedIn, UserSignedOut)
  def onNotify(e: EventName): Unit = e match {
    case UserSignedIn  => isAllowedToComment.value = true
    case UserSignedOut => isAllowedToComment.value = false
  }

  // -------------------------------------------------------------------------------------------------------------------

  // a binding post document
  private val bPostDoc: Var[Doc[Post]] = Var(Doc("", Post(None, None, None, None), "", ""))

  // a binding comment
  private case class BComment(
    author: Var[String],
    date: Var[String],
    text: Var[String]
  )

  // the comments on this page
  private val bComments = Vars.empty[BComment]

  @html private def comments() =
    for (c <- bComments)
      yield <div>{c.text.bind} ({c.author.bind}, {c.date.bind})</div>

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
      .createComment(user.accessToken, bPostDoc.value.name, c)
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
