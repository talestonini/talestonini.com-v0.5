package com.talestonini.pages

import com.talestonini.App.{user, handleClickSignIn}
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.utils._
import com.talestonini.utils.javascript.display
import com.talestonini.utils.observer.EventName._
import com.talestonini.utils.observer.Observer
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.BindingSeq
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.HTMLTextAreaElement
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

trait BasePostPage extends Observer {

  // --- UI ------------------------------------------------------------------------------------------------------------

  // where each post builds its content, converted from MarkDown to HTML by Laika
  def postContent(): Binding[Node]

  @html def apply(): Binding[Node] =
    <div>
      <div class="post-title w3-padding-8">{bPostDoc.bind.fields.title.getOrElse("")}</div>
      <div><i>{bPostDoc.bind.fields.publishDate.map(pd => datetime2Str(pd)).getOrElse("")}</i></div>
      <div class="post-content w3-padding-16">{postContent()}</div>
      <hr></hr>
      <div class="post-comments">
        <div class="header">Comments ({bComments.length.bind.toString})</div>
        <div class="">{commentInput()}</div>
        {comments()}
      </div>
    </div>

  // widget for inputting a new commment
  @html private def commentInput(): Binding[Node] = {
    val initComment = "What do you think?"
    val bText       = Var(initComment)

    val bTextArea: NodeBinding[HTMLTextAreaElement] =
      <textarea class="w3-input w3-border" rows="3" value={bText.bind} onfocus={e: Event => bText.value = ""} />

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

    val signInToCommentHandler = { e: Event =>
      handleClickSignIn()
      window.scrollTo(0, 0)
    }

    val signInToComment: Binding[Node] =
      <div style={s"display:${display(!isAllowedToComment.bind)}"}>
        <p>Please <a style="text-decoration: underline; cursor: pointer"
          onclick={signInToCommentHandler}>sign in</a> to comment</p>
      </div>

    val buttonClasses = "w3-btn w3-padding w3-black"
    val commentWidgets: Binding[Node] =
      <div style={s"display:${display(isAllowedToComment.bind)}"}>
        <div class="input">{bTextArea.bind}</div>
        <button type="button" class={buttonClasses} onclick={commentButtonHandler}>Comment</button>
        <button type="button" class={buttonClasses} onclick={cancelButtonHandler}>Cancel</button>
      </div>

    val div =
      <div class="">
        {signInToComment}
        {commentWidgets}
      </div>

    div
  }

  @html private def comments() =
    for (c <- bComments) yield aComment(c)

  @html private def aComment(c: BComment): Binding[Node] =
    <div class="w3-panel w3-light-grey w3-leftbar">
      <p><i>"{c.text.bind}"</i></p>
      <p>{c.author.bind} - <i>{c.date.bind}</i></p>
    </div>

  // --- public --------------------------------------------------------------------------------------------------------

  // retrieve the comments from db
  // when the promise for the post document which this page's comments belong to fulfills
  val postDocPromise = Promise[Doc[Post]]() // promise for the post document backing this page
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

  // observe user signing in/out to allow or not commenting on the post
  private var isAllowedToComment = Var(false)
  user.register(this, UserSignedIn, UserSignedOut)
  def onNotify(e: EventName): Unit = e match {
    case UserSignedIn  => isAllowedToComment.value = true
    case UserSignedOut => isAllowedToComment.value = false
  }

  // --- private -------------------------------------------------------------------------------------------------------

  // the binding post document backing this post page
  private val bPostDoc: Var[Doc[Post]] = Var(Doc("", Post(None, None, None, None), "", ""))

  // a binding comment
  private case class BComment(
    author: Var[String],
    date: Var[String],
    text: Var[String]
  )

  // the comments on this page
  private val bComments = Vars.empty[BComment]

  // persist new comment into db
  private def persistComment(comment: String): Unit = {
    val dbUser = com.talestonini.db.model.User(
      name = Option(user.displayName.value),
      email = Option(user.email.value),
      uid = Option(user.uid.value)
    )
    val c = Comment(
      author = Option(dbUser),
      date = Option(now()),
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
