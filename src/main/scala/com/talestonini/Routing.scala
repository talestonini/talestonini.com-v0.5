package com.talestonini

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages._
import pages.posts._

object Routing {

  case class Page(hash: String, content: Var[Binding[Node]])

  val pageMap: Map[String, Binding[Node]] = Map(
    ""      -> Home(),
    "about" -> About(),
    "posts" -> Posts(),
    "tags"  -> UnderConstruction(),
    // posts
    "capstone" -> Capstone(),
    "rapids"   -> Rapids()
  )

  def hash2Page(hash: String): Page =
    Page(s"#/$hash", Var(pageMap.get(hash).getOrElse(throw new Exception("page not found"))))

  var pages = for (hash <- pageMap.keys) yield hash2Page(hash)

  val route = Route.Hash(hash2Page(""))(new Route.Format[Page] {
    override def unapply(hashText: String) = pages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}
