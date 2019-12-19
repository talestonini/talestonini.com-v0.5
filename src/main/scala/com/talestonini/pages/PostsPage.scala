package com.talestonini.pages

import java.time._
import java.time.format.DateTimeFormatter.{ofPattern => pattern}

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import org.scalajs.dom.raw.{Node, Event}

import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.{Var, Vars}

import com.talestonini.Firebase, com.talestonini.Firebase._


object PostsPage {

  private case class BPost(title: Var[String], publishDate: Var[String])

  private val bPosts = Vars.empty[BPost]

  Firebase.getAuthToken()
    .onComplete({
      case token: Success[String] => 
        Firebase.getPosts(token.get)
          .onComplete({
            case posts: Success[Posts] =>
              for (p <- posts.get)
                bPosts.value += BPost(Var(p.fields.title.get), Var(datetime2Str(p.fields.publishDate)))
            case f: Failure[Posts] =>
              println(s"failure getting posts: ${f.exception.getMessage()}")
          })
      case f: Failure[String] => 
        println(s"failure getting auth token: ${f.exception.getMessage()}")
    })

  @dom
  def apply(): Binding[Node] =
    <div>
      {
        for (p <- bPosts) yield
          <p>{p.title.bind} ({p.publishDate.bind})</p>
      }
    </div>
    
  private val SimpleDateFormatter = pattern("dd/MM/yyyy")

  private def datetime2Str(datetime: Option[LocalDateTime]): String =
    if (datetime.isDefined)
      datetime.get.format(SimpleDateFormatter)
    else
      "no date"

}

