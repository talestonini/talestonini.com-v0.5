package com.talestonini.pages

import java.time._
import java.time.format.DateTimeFormatter.{ofPattern => pattern}

import com.talestonini.db.Firebase, com.talestonini.db.Firebase._
import com.talestonini.db.model._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.{Node, Event}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Posts {

  private case class BPost(
    restEntityLink: Var[String],
    title: Var[String],
    resource: Var[String],
    publishDate: Var[String]
  )

  private val bPosts = Vars.empty[BPost]

  Firebase
    .getAuthToken()
    .onComplete({
      case token: Success[String] =>
        Firebase
          .getPosts(token.get)
          .onComplete({
            case posts: Success[Posts] =>
              for (p <- posts.get)
                bPosts.value += BPost(
                  Var(p.name),
                  Var(p.fields.title.get),
                  Var(p.fields.resource.get),
                  Var(datetime2Str(p.fields.publishDate))
                )
            case f: Failure[Posts] =>
              println(s"failure getting posts: ${f.exception.getMessage()}")
          })
      case f: Failure[String] =>
        println(s"failure getting auth token: ${f.exception.getMessage()}")
    })

  @html def apply(): Binding[Node] =
    <div>{postItems()}</div>

  @html private def postItems() =
    for (p <- bPosts)
      yield <p><a href={s"#/${p.resource.bind}"}>{p.title.bind}</a> ({p.publishDate.bind})</p>

  private val SimpleDateFormatter = pattern("dd/MM/yyyy")

  private def datetime2Str(datetime: Option[LocalDateTime]): String =
    if (datetime.isDefined)
      datetime.get.format(SimpleDateFormatter)
    else
      "no date"

}
