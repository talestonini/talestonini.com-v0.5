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
  def logo =
    <div class="w3-col logo">
      <table>
        <tr>
          <td class="symbol">&#x276F;</td>
          <td class="tales_t">T</td>
          <td class="ales">ales</td>
          {pronunciation("/tɑː \u2022 les/").bind}
        </tr>
        <tr>
          <td></td>
          <td class="tonini_t">T</td>
          <td class="onini">onini</td>
          {pronunciation("/toʊ \u2022 niː \u2022 nɪ/").bind}
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
  def pronunciation(p: String) = 
    <td class="pronunciation">
      <a href="https://www.oxfordlearnersdictionaries.com/about/english/pronunciation_english" target="_blank">{p}</a>
    </td>

  @dom
  def menu: Binding[Node] = {
    val classes = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-padding-10 w3-hide-small"
    <div class="w3-rest">
      <div class="menu">
        <a href="#" class={classes}>Link 1</a>
        <a href="#" class={classes}>Link 2</a>
        <a href="#" class={classes}>Link 3</a>
      </div>
    </div>
  }

  @dom
  def mobileMenu: Binding[Node] = {
    val classes = "w3-bar-item w3-button w3-padding-large"
    <div class="w3-bar-block w3-hide-large w3-hide-medium">
      <div class="mobile-menu">
        <a href="#" class={classes}>Link 1</a>
        <a href="#" class={classes}>Link 2</a>
        <a href="#" class={classes}>Link 3</a>
      </div>
    </div>
  }

  @dom
  def page: Binding[Node] = {
    <div>
      <div class="w3-row">
        {logo.bind}
        {menu.bind}
      </div>
      {mobileMenu.bind}
      <hr></hr>

      <div class="w3-content content">
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

      <hr></hr>
      {footer.bind}
    </div>
  }

  @dom
  def footer: Binding[Node] =
    <footer class="footer">
      <p>Footer</p>
    </footer>

  @JSExport("main")
  def main(): Unit = {
    dom.render(document.body, page)
  }

}
