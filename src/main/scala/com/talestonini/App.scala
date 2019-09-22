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
  def page: Binding[BindingSeq[Node]] = {
    <div class="logo">
      <div>
        <table>
          <tr>
            <td class="symbol">&#x276F;</td>
            <td class="tales_t">T</td>
            <td class="ales">ales</td>
          </tr>
          <tr>
            <td></td>
            <td class="tonini_t">T</td>
            <td class="onini">onini</td>
          </tr>
          <tr>
            <td></td>
            <td class="dot">.</td>
            <td class="com">com</td>
          </tr>
        </table>
      </div>
    </div>

    <div class="topnav">
      <a href="#">Link</a>
      <a href="#">Link</a>
      <a href="#">Link</a>
      <a href="#">Link</a>
      <a href="#">Link</a>
    </div>

    <div class="content">
      <h2>CSS Template</h2>
      <p>A topnav, content and a footer.</p>

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

    <div class="footer">
      <p>Footer</p>
    </div>
  }

  @JSExport("main")
  def main(): Unit = {
    dom.render(document.body, page)
  }

}
