package com.talestonini.pages

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Posts {

  @html def apply(): Binding[Node] =
    <div>{postLinks()}</div>

  // a binding post link
  case class BPostLink(
    title: Var[String],
    publishDate: Var[String],
    resource: Var[String]
  )

  val bPostLinks = Vars.empty[BPostLink]

  @html private def postLinks() =
    for (p <- bPostLinks)
      yield <div>
              <p>
                <a href={s"#/${p.resource.bind}"}>{p.title.bind}</a>
                <div class="post-date"><i>{p.publishDate.bind}</i></div>
              </p>
            </div>

}
