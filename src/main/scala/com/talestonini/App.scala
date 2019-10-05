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
  def pronunciationTd(p: String) = 
    <td class="pronunciation">
      <a href="https://www.oxfordlearnersdictionaries.com/about/english/pronunciation_english" target="_blank">
        { p }
      </a>
    </td>

  @dom
  def logoDiv =
    <div class="logo">
      <table>
        <tr>
          <td class="symbol">&#x276F;</td>
          <td class="tales_t">T</td>
          <td class="ales">ales</td>
          { pronunciationTd("/tɑː \u2022 les/").bind }
        </tr>
        <tr>
          <td></td>
          <td class="tonini_t">T</td>
          <td class="onini">onini</td>
          { pronunciationTd("/toʊ \u2022 niː \u2022 nɪ/").bind }
        </tr>
        <tr>
          <td></td>
          <td class="dot">&#x2022;</td>
          <td class="com">com</td>
          <td></td>
        </tr>
      </table>
    </div>

  @dom
  def page: Binding[Node] = {
    val menuItemClasses = "w3-bar-item w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-padding-16"
    <div class="page">
      <div class="header">
        {logoDiv.bind}
        <div class="w3-bar topnav">
          <a href="#" class={menuItemClasses}>Link 1</a>
          <a href="#" class={menuItemClasses}>Link 2</a>
          <a href="#" class={menuItemClasses}>Link 3</a>
        </div>
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
    </div>
  }

  @JSExport("main")
  def main(): Unit = {
    dom.render(document.body, page)
  }

}
