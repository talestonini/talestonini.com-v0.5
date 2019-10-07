package com.talestonini

import org.scalajs.dom.raw.Node
import org.scalajs.dom.window

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom, Route}

import pages.{ItemTwo, About}


object Routing {

  case class Page(name: String, hash: String, content: Var[Binding[Node]])
  
  @dom
  val homeContent: Binding[Node] =
    <div>
      <p>Home page content...</p>
    </div>
  
  @dom
  val itemOneContent: Binding[Node] =
    <div>
      <p>Page under construction...</p>
      <p><a href="#/">Home</a></p>
    </div>

  // app pages
  val home = Page("Home", "#/", Var(homeContent))
  val itemOne = Page("Item 1", "#/itemOne", Var(itemOneContent))
  val itemTwo = Page("Item 2", "#/itemTwo", Var(ItemTwo()))
  val about = Page("About", "#/about", Var(About()))

  val allPages = Vector(home, itemOne, itemTwo, about)
  val route = Route.Hash(home)(new Route.Format[Page] {
    override def unapply(hashText: String) = allPages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}

