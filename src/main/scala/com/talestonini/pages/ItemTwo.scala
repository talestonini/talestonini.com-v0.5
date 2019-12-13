package com.talestonini.pages

import scala.util.{Failure, Success}
import org.scalajs.dom.raw.Node
import org.scalajs.dom.raw.Event

import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, dom}

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.response.SimpleHttpResponse
import fr.hmil.roshttp.Protocol.HTTPS
import fr.hmil.roshttp.Method.POST
import fr.hmil.roshttp.body.PlainTextBody
import monix.execution.Scheduler.Implicits.global
import io.circe._, io.circe.parser._


object ItemTwo {

  case class Contact(name: Var[String], email: Var[String])

  val data = Vars.empty[Contact]

  var token: Var[String] = Var("nothing yet...")

  val ApiKey = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY"
  val ProjectId = "ttdotcom"
  val Database = "(default)"
  val FirestoreApi = "firestore.googleapis.com/v1"

  val commonHeaders = {
    "Access-Control-Allow-Origin" -> "*"
    "Access-Control-Allow-Headers" -> "Content-Type"
    "Access-Control-Allow-Methods" -> "POST"
    "Content-Type" -> "application/json"
  }

  def getAuthToken() =
    HttpRequest()
      .withMethod(POST)
      .withProtocol(HTTPS)
      .withHost("identitytoolkit.googleapis.com")
      .withPath("/v1/accounts:signUp")
      .withQueryParameter("key", ApiKey)
      .withHeaders(commonHeaders)
      .send()
      .onComplete({
        case rawJson: Success[SimpleHttpResponse] => 
          val res: Json = parse(rawJson.get.body).getOrElse(Json.Null)
          token.value = (res \\ "idToken")(0).toString
        case e: Failure[SimpleHttpResponse] => 
          token.value = "failure..."
      })

  def requestComments(): Unit = {
    val req = HttpRequest(s"{FirestoreApi}/projects/{ProjectId}/databases/{Database}/documents/comments")
  }

  @dom
  def apply(): Binding[Node] = {
    getAuthToken()
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
                  <button onclick={event: Event =>
                    contact.name.value = "Modified Name"
                  }>Modify the name</button>
                </td>
              </tr>
            }
          }
          <tr>
            <td>{token.bind}</td>
          </tr>
        </tbody>
      </table>
    </div>
  }
    
}

