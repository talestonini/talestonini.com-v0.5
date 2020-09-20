package com.talestonini

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages._
import posts._

object Routing {

  case class Page(hash: String, content: Var[Binding[Node]])

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
  val home      = Page("#/", Var(homeContent))
  val postsPage = Page("#/posts", Var(PostsPage()))
  val tagsPage  = Page("#/tags", Var(underConstructionContent))
  val about     = Page("#/about", Var(AboutPage()))

  var allPages = Seq(home, postsPage, tagsPage, about)

  def newPostPage(resource: String): Page =
    Page(s"#/$resource", Var(Posts.get(resource).getOrElse(throw new Exception("post is missing"))))

  // add posts to all pages
  for (p <- Posts.keys)
    allPages = allPages :+ newPostPage(p)

  val route = Route.Hash(home)(new Route.Format[Page] {
    override def unapply(hashText: String) = allPages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}
