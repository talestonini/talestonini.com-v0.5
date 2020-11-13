package com.talestonini.pages

import com.talestonini.pages._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Posts {

  @html def apply(): Binding[Node] =
    <div>{postItems()}</div>

  val bPosts = Vars.empty[BPost]

  // -------------------------------------------------------------------------------------------------------------------

  @html private def postItems() =
    for (p <- bPosts)
      yield <p><a href={s"#/${p.resource.bind}"}>{p.title.bind}</a> ({p.publishDate.bind})</p>

}
