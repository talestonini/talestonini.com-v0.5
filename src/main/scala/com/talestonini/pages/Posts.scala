package com.talestonini.pages

import com.talestonini.db.Firebase, com.talestonini.db.Firebase._
import com.talestonini.db.model._
import com.talestonini.utils._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.{Node, Event}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Posts {

  /**
    * A binding post.
    */
  private case class BPost(
    restEntityLink: Var[String],
    title: Var[String],
    resource: Var[String],
    publishDate: Var[String]
  )

  private val bPosts = Vars.empty[BPost]

  //Firebase
  //.getPosts()
  //.onComplete({
  //case posts: Success[Posts] =>
  //for (p <- posts.get)
  //bPosts.value += BPost(
  //restEntityLink = Var(p.name),
  //title = Var(p.fields.title.get),
  //resource = Var(p.fields.resource.get),
  //publishDate = Var(datetime2Str(p.fields.publishDate))
  //)
  //case f: Failure[Posts] =>
  //println(s"failure getting posts: ${f.exception.getMessage()}")
  //})

  @html def apply(): Binding[Node] =
    <div>{postItems()}</div>

  @html private def postItems() =
    for (p <- bPosts)
      yield <p><a href={s"#/${p.resource.bind}"}>{p.title.bind}</a> ({p.publishDate.bind})</p>

}
