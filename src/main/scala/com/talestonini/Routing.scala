package com.talestonini

import com.talestonini.App.user
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.pages._
import com.talestonini.utils._
import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages._
import pages.posts._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

object Routing {

  private val postDocNameMap: Map[String, Promise[String]] = Map(
    "capstone" -> Capstone.postDocNamePromise,
    "rapids"   -> Rapids.postDocNamePromise
  )

  private val pageMap: Map[String, Binding[Node]] = Map(
    ""      -> Home(),
    "about" -> About(),
    "posts" -> Posts(),
    "tags"  -> UnderConstruction(),
    // posts
    "capstone" -> Capstone(),
    "rapids"   -> Rapids()
  )

  private val pages = for (hash <- pageMap.keys) yield hash2Page(hash)

  // -------------------------------------------------------------------------------------------------------------------

  // retrieve posts from db at application start
  CloudFirestore
    .getPosts()
    .onComplete({
      case posts: Success[Docs[Post]] =>
        for (p <- posts.get) {
          val resource = p.fields.resource.get

          // instantiate a binding post
          val bPost = BPost(
            docName = Var(p.name),
            title = Var(p.fields.title.get),
            resource = Var(resource),
            publishDate = Var(datetime2Str(p.fields.publishDate))
          )

          // post document names are needed to retrieve their (child) comments
          val postDocName = p.name
          postDocNameMap
            .get(resource)
            .getOrElse(
              throw new Exception(s"missing entry in postDocNameMap for $resource")
            ) success postDocName

          Posts.bPosts.value += bPost
        }
      case f: Failure[Docs[Post]] =>
        println(s"failed getting posts: ${f.exception.getMessage()}")
    })

  case class Page(hash: String, content: Var[Binding[Node]])

  def hash2Page(hash: String): Page =
    Page(s"#/$hash", Var(pageMap.get(hash).getOrElse(throw new Exception("page not found"))))

  val route = Route.Hash(hash2Page(""))(new Route.Format[Page] {
    override def unapply(hashText: String) = pages.find(_.hash == window.location.hash)
    override def apply(page: Page): String = page.hash
  })
  route.watch()

}
