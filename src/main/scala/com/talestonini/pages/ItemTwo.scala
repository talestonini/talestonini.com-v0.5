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

  case class Contact(name: Var[String], email: Var[String])
  val data = Vars.empty[Contact]

  case class BindingPost(title: Var[String], publishDate: Var[String])
  val posts = Vars.empty[BindingPost]

  def datetime2Str(datetime: Option[LocalDateTime], 
                   dtf: DateTimeFormatter = ofPattern("dd/MM/yyyy")): String =
    if (datetime.isDefined)
      datetime.get.format(dtf)
    else
      "no date"

  lazy val getPosts = () => 
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
    <div>
      <div>
        <button onclick={ event: Event =>
          data.value += Contact(Var("Tales Tonini"), Var("talestonini@gmail.com"))
        }>Add a contact</button>
      </div>
      <div>
        <button onclick={ event: Event =>
          getPosts()
        }>Get Posts</button>
      </div>

      <table border="1" cellPadding="5">
        <thead>
          <tr>
            <th>Name</th>
            <th>E-mail</th>
            <th>Operation</th>
          </tr>
        </thead>
        <tbody>
          {
            for (contact <- data) yield {
              <tr>
                <td>{contact.name.bind}</td>
                <td>{contact.email.bind}</td>
                <td>
                  <button onclick={event: Event =>
                    contact.name.value = "Modified Name"
                  }>Modify the name</button>
                </td>
              </tr>
            }
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
  }
    
}

