package com.talestonini

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages._
import pages.posts._
import scala.concurrent.Promise
import com.talestonini.db.Firebase
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import com.talestonini.db.model._

object Routing {

  val postRestEntityLinkMap: Map[String, Promise[String]] = Map(
    "capstone" -> Capstone.postRestEntityLinkPromise,
    "rapids"   -> Rapids.postRestEntityLinkPromise
  )

  val pageMap: Map[String, Binding[Node]] = Map(
    ""         -> Home(),
    "about"    -> About(),
    "posts"    -> Posts(),
    "tags"     -> UnderConstruction(),
    "loggedIn" -> LoggedIn(),
    // posts
    "capstone" -> Capstone(),
    "rapids"   -> Rapids()
  )

  //Firebase
  //.getPosts()
  //.onComplete({
  //case posts: Success[Posts] =>
  //for (p <- posts.get) {
  //val resource = p.fields.resource.get
  //postRestEntityLinkMap
  //.get(resource)
  //.getOrElse(throw new Exception(s"missing entry in postRestEntityLinkMap for $resource")) success p.name
  //}
  //case f: Failure[Posts] =>
  //println(s"failure getting posts: ${f.exception.getMessage()}")
  //})

  case class Page(hash: String, content: Var[Binding[Node]])

  def hash2Page(hash: String): Page =
    Page(s"#/$hash", Var(pageMap.get(hash).getOrElse(throw new Exception("page not found"))))

  var pages = for (hash <- pageMap.keys) yield hash2Page(hash)

  val route = Route.Hash(hash2Page(""))(new Route.Format[Page] {
    override def unapply(hashText: String) = pages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}
