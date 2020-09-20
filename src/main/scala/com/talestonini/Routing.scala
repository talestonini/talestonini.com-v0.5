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

  val homePage = Page("#/", Var(Home()))
  val otherPages: Map[String, Binding[Node]] = Map(
    "about" -> About(),
    "posts" -> Posts(),
    "tags"  -> UnderConstruction(),
    // posts
    "capstone" -> Capstone(),
    "rapids"   -> Rapids()
  )

  def newPage(hash: String): Page =
    Page(s"#/$hash", Var(otherPages.get(hash).getOrElse(throw new Exception("page not found"))))

  var allPages = Seq(homePage)
  for (p <- otherPages.keys)
    allPages = allPages :+ newPage(p)

  val route = Route.Hash(homePage)(new Route.Format[Page] {
    override def unapply(hashText: String) = allPages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}
