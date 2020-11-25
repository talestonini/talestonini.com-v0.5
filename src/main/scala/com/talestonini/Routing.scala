package com.talestonini

import com.talestonini.App.isLoading
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.pages.BasePostPage
import com.talestonini.utils._
import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, Route}
import org.scalajs.dom.raw.Node
import org.scalajs.dom.window
import pages.posts._
import pages.{Home, Posts, Tags, About}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

object Routing {

  // comment out if the post is not to be available in the website
  val postPageObjMap: Map[String, BasePostPage] = Map(
    //"funProgCapstone"      -> FunProgCapstone,
    "morseCodeChallenge"   -> MorseCodeChallenge,
    "urbanForestChallenge" -> UrbanForestChallenge
  )

  private val pageMap: Map[String, Binding[Node]] = Map(
    ""      -> Home(),
    "posts" -> Posts(),
    "tags"  -> Tags(),
    "about" -> About()
  ) ++ postPageObjMap.map(e => (e._1, e._2.apply()))

  private val postDocMap: Map[String, Promise[Doc[Post]]] = Map(
    "funProgCapstone"      -> FunProgCapstone.postDocPromise,
    "morseCodeChallenge"   -> MorseCodeChallenge.postDocPromise,
    "urbanForestChallenge" -> UrbanForestChallenge.postDocPromise
  )

  private val pages = for (hash <- pageMap.keys) yield hash2Page(hash)

  // retrieve posts from db at application start
  val retrievingPosts = "retrievingPosts"
  displayLoading(isLoading, retrievingPosts)
  CloudFirestore
    .getPosts()
    .onComplete({
      case posts: Success[Docs[Post]] =>
        for (p <- posts.get) {
          val resource = p.fields.resource.get

          // to build the posts page, with the list of posts
          if (pageMap.keySet.contains(resource))
            Posts.bPostLinks.value += Posts.BPostLink(
              title = Var(p.fields.title.get),
              publishDate = Var(datetime2Str(p.fields.publishDate)),
              resource = Var(resource)
            )

          // to build each post page
          postDocMap
            .get(resource)
            .getOrElse(
              throw new Exception(s"missing entry in postDocMap for $resource")
            ) success p
        }
        hideLoading(isLoading, retrievingPosts)
      case f: Failure[Docs[Post]] =>
        println(s"failed getting posts: ${f.exception.getMessage()}")
        hideLoading(isLoading, retrievingPosts)
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
