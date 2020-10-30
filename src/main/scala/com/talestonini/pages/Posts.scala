package com.talestonini.pages

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

  @html def apply(): Binding[Node] =
    <div>{postItems()}</div>

  // a binding post
  case class BPost(
    docName: Var[String],
    title: Var[String],
    resource: Var[String],
    publishDate: Var[String]
  )

  val bPosts = Vars.empty[BPost]

  // -------------------------------------------------------------------------------------------------------------------

  @html private def postItems() =
    for (p <- bPosts)
      yield <p><a href={s"#/${p.resource.bind}"}>{p.title.bind}</a> ({p.publishDate.bind})</p>

}
