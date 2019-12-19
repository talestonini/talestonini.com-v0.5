package com.talestonini.pages

import java.time._
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import org.scalajs.dom.raw.Node
import org.scalajs.dom.raw.Event

import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, dom}

import com.talestonini.Firebase
import com.talestonini.Firebase._


object ItemTwo {

  case class BindingPost(title: Var[String], publishDate: Var[String])
  val posts = Vars.empty[BindingPost]

  def datetime2Str(datetime: Option[LocalDateTime], dtf: DateTimeFormatter = ofPattern("dd/MM/yyyy")): String =
    if (datetime.isDefined)
      datetime.get.format(dtf)
    else
      "no date"

  def getPosts() = 
    Firebase.getAuthToken()
      .onComplete({
        case res: Success[String] => 
          val token = res.get
          Firebase.getPosts(token)
            .onComplete({
              case res: Success[Posts] =>
                for (p <- res.get) {
                  posts.value += BindingPost(Var(p.fields.title.get), 
                                             Var(datetime2Str(p.fields.publishDate)))
                }
              case res: Failure[Posts] =>
            })
        case res: Failure[String] => 
      })

  @dom
  def apply(): Binding[Node] = {
    val itemTwo = <div>
      <table border="1" cellPadding="5">
        <thead>
          <tr>
            <th>Post</th>
            <th>Published</th>
          </tr>
        </thead>
        <tbody>
          {
            for (p <- posts) yield {
              <tr>
                <td>{p.title.bind}</td>
                <td>{p.publishDate.bind}</td>
              </tr>
            }
          }
        </tbody>
      </table>
    </div>
    getPosts()
    itemTwo
  }
    
}

