package com.talestonini

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages._
import posts._

object Routing {

  case class Page(name: String, hash: String, content: Var[Binding[Node]])

  @html val homeContent: Binding[Node] =
    <div>
      <p>Home page content...</p>
    </div>

  @html val underConstructionContent: Binding[Node] =
    <div>
      <p>Page under construction...</p>
      <p><a href="#/">Home</a></p>
    </div>

  // app pages
  val home      = Page("Home", "#/", Var(homeContent))
  val postsPage = Page("Posts", "#/posts", Var(PostsPage()))
  val tagsPage  = Page("Tags", "#/tags", Var(underConstructionContent))
  val about     = Page("About", "#/about", Var(AboutPage()))

  // posts
  val capstonePost = Page("Capstone", "#/capstone", Var(Capstone()))
  val rapidsPost   = Page("Rapids", "#/rapids", Var(Rapids()))

  val allPages = Vector(home, postsPage, tagsPage, about, capstonePost, rapidsPost)
  val route = Route.Hash(home)(new Route.Format[Page] {
    override def unapply(hashText: String) = allPages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}
