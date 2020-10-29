package com.talestonini

import com.talestonini.App.user
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.utils._
import com.talestonini.utils.observer.{EventName, Observer}
import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages._
import pages.posts._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

object Routing extends Observer {

  val postDocNameMap: Map[String, Promise[String]] = Map(
    "capstone" -> Capstone.postDocNamePromise,
    "rapids"   -> Rapids.postDocNamePromise
  )

  val pageMap: Map[String, Binding[Node]] = Map(
    ""      -> Home(),
    "about" -> About(),
    "posts" -> Posts(),
    "tags"  -> UnderConstruction(),
    // posts
    "capstone" -> Capstone(),
    "rapids"   -> Rapids()
  )

  user.register(this, "userLoggedIn")

  def onNotify(e: EventName): Unit = e match {
    case "userLoggedIn" =>
      CloudFirestore
        .getPosts(user.accessToken)
        .onComplete({
          case posts: Success[Posts] =>
            for (p <- posts.get) {
              val resource = p.fields.resource.get

              // post REST entity links enable retrieving comments (any entity dependent on posts)
              val postDocName = p.name
              postDocNameMap
                .get(resource)
                .getOrElse(
                  throw new Exception(s"missing entry in postDocNameMap for $resource")
                ) success postDocName

              // bPosts (binding posts) help build the Posts page
              Posts.bPosts.value += Posts.BPost(
                docName = Var(postDocName),
                title = Var(p.fields.title.get),
                resource = Var(resource),
                publishDate = Var(datetime2Str(p.fields.publishDate))
              )
            }
          case f: Failure[Posts] =>
            println(s"failure getting posts: ${f.exception.getMessage()}")
        })
  }

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
