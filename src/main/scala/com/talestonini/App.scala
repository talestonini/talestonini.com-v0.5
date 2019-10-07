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
  def page: Binding[Node] =
    <div>
      <div class="w3-row">
        {Logo().bind}
        {Menu().bind}
      </div>
      <hr></hr>

      <div class="w3-content content">
        {route.state.bind.content.value.bind}
      </div>

      <hr></hr>
      {Footer().bind}
    </div>

  @JSExport("main")
  def main(): Unit =
    dom.render(document.body, page)

}

