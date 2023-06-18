package com.talestonini.pages

import cats.effect.unsafe.implicits.global
import com.talestonini.App.isLoading
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.utils._
import com.talestonini.utils.javascript.display
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.{HTMLInputElement, HTMLTextAreaElement}
import org.scalajs.dom.raw.Node
import scala.concurrent.Promise
import scala.scalajs.concurrent.JSExecutionContext.queue
import scala.util.{Failure, Success}

trait BasePostPage {

  // --- UI ------------------------------------------------------------------------------------------------------------

  // where each post builds its content, converted from MarkDown to HTML by Laika
  def postContent(): Binding[Node]

  private val lin     = "https://www.linkedin.com/sharing/share-offsite/?mini=true&url="
  private val twitter = "https://twitter.com/intent/tweet?url="
  private val tt      = "https%3A%2F%2Ftalestonini.com%2F%23%2F"

  @html def apply(): Binding[Node] =
    <div>
      <div class="post-date w3-padding-16 w3-display-container">
        <div class="w3-display-left">{postDate(bPostDoc.bind.fields)}</div>
        <div class="share-post w3-display-right">
        {shareAnchor("fa-linkedin", lin + tt + bPostDoc.bind.fields.resource.getOrElse(""), "Share on LinkedIn")}
        {shareAnchor("fa-twitter", twitter + tt + bPostDoc.bind.fields.resource.getOrElse(""), "Share on Twitter")}
        {
      shareAnchor("fa-link", s"javascript:copyToClipboard('${tt + bPostDoc.bind.fields.resource.getOrElse("")}')",
        "Copy link", "_self")
    }
        </div>
      </div>
      <div class="post-title w3-padding-8">{bPostDoc.bind.fields.title.getOrElse("")}</div>
      <div class="w3-padding-16 line-numbers">{postContent()}</div>
      <hr />
      <div class="post-comments">
        <div class="header w3-bold">Comments ({bComments.length.bind.toString})</div>
        {commentInput()}
        {comments()}
      </div>
    </div>

  @html private def shareAnchor(anchorIcon: String, href: String, tooltipText: String,
    anchorTarget: String = "_blank"): Binding[Node] =
    <a href={href} class="w3-tooltip no-decoration" target={anchorTarget}>
      <i class={"fa " + anchorIcon + " w3-hover-opacity"} />
      <span class="tooltip w3-text w3-tag w3-small">{tooltipText}</span>
    </a>

  @html private def postDate(p: Post): Binding[Node] = {
    val firstPublishDate = p.firstPublishDate.map(fpd => datetime2Str(fpd, SimpleDateFormatter)).getOrElse("")
    val publishDate      = p.publishDate.map(pd => datetime2Str(pd, SimpleDateFormatter)).getOrElse("")

    if (firstPublishDate == publishDate)
      <i>{publishDate}</i>
    else
      <div>
        <i>{publishDate}</i> <i class="first-published">(first {firstPublishDate})</i>
      </div>
  }

  // widget for inputting a new commment
  private val isInputtingComment = Var(false)
  @html private def commentInput(): Binding[Node] = {
    val initComment = "What do you think?"
    val bComment    = Var(initComment)
    val initName    = "Name"
    val bName       = Var(initName)

    val commentFocusHandler = { e: Event =>
      bComment.value = ""
      isInputtingComment.value = true
    }

    val nameFocusHandler = { e: Event =>
      bName.value = ""
    }

    val bTextArea: NodeBinding[HTMLTextAreaElement] =
      <textarea class="w3-input w3-border" placeholder={initComment} rows="1" value={bComment.bind}
        onclick="this.rows = '5'" onfocus={commentFocusHandler} />

    val bInput: NodeBinding[HTMLInputElement] =
      <input class="w3-input w3-border" type="text" placeholder={initName} value={bName.bind}
        onfocus={nameFocusHandler} />

    def cleanCommentInputs() = {
      bComment.value = initComment
      bName.value = initName
      isInputtingComment.value = false
      bTextArea.value.rows = 1
    }

    val commentButtonHandler = { e: Event =>
      if (bTextArea.value.value.nonEmpty) {
        persistComment(bInput.value.value, bTextArea.value.value)
        cleanCommentInputs()
      }
    }

    val cancelButtonHandler = { e: Event => cleanCommentInputs() }

    val buttonClasses = "w3-button w3-ripple w3-padding w3-black"
    val commentInputControls: Binding[Node] =
      <div class="w3-panel w3-light-grey w3-leftbar w3-padding-16">
        {bTextArea.bind}
        <div class="w3-padding-8" style={s"display: ${display(isInputtingComment.bind)}"}>
          {bInput.bind}
        </div>
        <div class="w3-right" style={s"display: ${display(isInputtingComment.bind)}"}>
          <div class="w3-bar">
            <button type="button" class={buttonClasses} onclick={commentButtonHandler}>Comment</button>
            <button type="button" class={buttonClasses} onclick={cancelButtonHandler}>Cancel</button>
          </div>
        </div>
      </div>

    commentInputControls
  }

  @html private def comments() =
    for (c <- bComments) yield aComment(c)

  @html private def aComment(c: BComment): Binding[Node] =
    <div class="w3-panel w3-light-grey w3-leftbar">
      <p><i>{c.text.bind}</i></p>
      <p>{c.author.bind}<span style="padding: 0 15px 0 15px">|</span><i>{c.date.bind}</i></p>
    </div>

  // --- public --------------------------------------------------------------------------------------------------------

  // retrieve the comments from db
  // when the promise for the post document which this page's comments belong to fulfills
  val postDocPromise = Promise[Doc[Post]]() // promise for the post document backing this page
  postDocPromise.future
    .onComplete({
      case postDoc: Success[Doc[Post]] =>
        bPostDoc.value = postDoc.get
        val retrievingComments = s"retrievingComments_${bPostDoc.value.fields.resource}"
        displayLoading(isLoading, retrievingComments)
        CloudFirestore
          .getComments(postDoc.get.name)
          .unsafeToFuture()
          .onComplete({
            case comments: Success[Docs[Comment]] =>
              for (c <- comments.get)
                bComments.value += BComment(
                  author = Var(c.fields.author.get.name.get),
                  date = Var(datetime2Str(c.fields.date)),
                  text = Var(c.fields.text.get)
                )
              hideLoading(isLoading, retrievingComments)
            case f: Failure[Docs[Comment]] =>
              println(s"failed getting comments: ${f.exception.getMessage()}")
              hideLoading(isLoading, retrievingComments)
          })(queue)
      case f: Failure[Doc[Post]] =>
        println(s"failed getting post document name: ${f.exception.getMessage()}")
    })(queue)

  // --- private -------------------------------------------------------------------------------------------------------

  // the binding post document backing this post page
  private val bPostDoc: Var[Doc[Post]] = Var(Doc("", Post(None, None, None, None), "", ""))

  // a binding comment
  private case class BComment(
    author: Var[String], date: Var[String], text: Var[String]
  )

  // the comments on this page
  private val bComments = Vars.empty[BComment]

  // persist new comment into db
  private def persistComment(name: String, comment: String): Unit = {
    val dbUser = com.talestonini.db.model.User(
      name = Option(name),
      email = Option("---"), // there is no auth anymore
      uid = Option("---")    // there is no auth anymore
    )
    val c = Comment(
      author = Option(dbUser),
      date = Option(now()),
      text = Option(comment)
    )
    CloudFirestore
      .createComment(bPostDoc.value.name, c)
      .unsafeToFuture()
      .onComplete({
        case doc: Success[Doc[Comment]] =>
          bComments.value.prepend(BComment(
              author = Var(doc.value.fields.author.get.name.get),
              date = Var(datetime2Str(doc.value.fields.date)),
              text = Var(doc.value.fields.text.get)
            ))
        case f: Failure[Doc[Comment]] =>
          println("failed creating comment")
      })(queue)
  }

}
