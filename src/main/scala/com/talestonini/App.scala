package com.talestonini

import org.scalajs.{dom => jsdom}
import jsdom.document
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}
import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.{Binding, dom}

import components.{Logo, Menu, Footer}


@JSExportTopLevel("App")
object App {

  import Routing._

  @dom
  def app: Binding[Node] =
    <div>
      <div class="w3-content w3-row w3-hide-small">
        <div class="w3-padding-16">
          {Logo().bind}
          {Menu().bind}
        </div>
        <hr></hr>
      </div>
      <div class="w3-content w3-row w3-hide-large w3-hide-medium">
        <div class="w3-padding-8">
          {Logo().bind}
          {Menu().bind}
        </div>
        <hr></hr>
      </div>

      <div class="w3-content">
        <div class="content">
          {route.state.bind.content.value.bind}
        </div>
        <hr></hr>
      </div>

      <footer class="w3-container w3-padding-16 w3-center w3-hide-small">
        {Footer().bind}
      </footer>
      <footer class="w3-container w3-padding-8 w3-center w3-hide-large w3-hide-medium">
        {Footer().bind}
      </footer>
    </div>

  @JSExport("main")
  def main(): Unit =
    dom.render(document.body, app)

}

