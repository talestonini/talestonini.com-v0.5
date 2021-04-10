package com.talestonini.pages

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Posts {

  @html def apply(): Binding[Node] =
    <div class="post-list">{postLinks()}</div>

  // a binding post link
  case class BPostLink(
    title: Var[String],
    firstPublishDate: Var[String],
    publishDate: Var[String],
    resource: Var[String]
  )

  val bPostLinks = Vars.empty[BPostLink]

  @html private def postLinks() =
    for (p <- bPostLinks)
      yield <div>
              <p>
                <a class="w3-bold" href={s"#/${p.resource.bind}"}>{p.title.bind}</a>
                <div class="post-date">
                  <a href={s"#/${p.resource.bind}"}>
                    <i>{p.publishDate.bind}</i> {firstPublishDate(p).bind}
                  </a>
                </div>
              </p>
            </div>

  @html private def firstPublishDate(p: BPostLink): Binding[Node] =
    if (p.firstPublishDate.value == p.publishDate.value)
      <span />
    else
      <i class="first-published">(first {p.firstPublishDate.bind})</i>

}
