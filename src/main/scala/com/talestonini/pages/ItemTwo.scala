package com.talestonini.pages

import org.scalajs.dom.raw.Node
import org.scalajs.dom.raw.Event

import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, dom}

import fr.hmil.roshttp.HttpRequest


object ItemTwo {

  case class Contact(name: Var[String], email: Var[String])

  val data = Vars.empty[Contact]

  val FirestoreApi = "https://firestore.googleapis.com/v1"
  val ProjectId = "ttdotcom"
  val Database = "(default)"

  def requestComments(): Unit = {
    val req = HttpRequest(s"{FirestoreApi}/projects/{ProjectId}/databases/{Database}/documents/comments")
  }

  @dom
  def apply(): Binding[Node] =
    <div>
      <div>
        <button onclick={ event: Event =>
          data.value += Contact(Var("Tales Tonini"), Var("talestonini@gmail.com"))
        }>Add a contact</button>
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
                  <button onclick={ event: Event =>
                    contact.name.value = "Modified Name"
                  }>Modify the name</button>
                </td>
              </tr>
            }
          }
        </tbody>
      </table>
    </div>
    
}

