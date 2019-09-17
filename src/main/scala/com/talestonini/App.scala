package com.talestonini

import org.scalajs.{dom => jsdom}
import jsdom.document
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}
import org.scalajs.dom.html.Table
import org.scalajs.dom.raw.Node
import org.scalajs.dom.raw.Event

import com.thoughtworks.binding.Binding.{Var, Vars}
import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.BindingSeq

case class Contact(name: Var[String], email: Var[String])

@JSExportTopLevel("App")
object App {

  val data = Vars.empty[Contact]

  @dom
  def table: Binding[BindingSeq[Node]] = {
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
  }

  @JSExport("main")
  def main(): Unit = {
    dom.render(document.body, table)
  }

}
