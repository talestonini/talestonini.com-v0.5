package com.talestonini

import com.thoughtworks.binding.Binding
import components.{Footer, Logo, Menu}
import org.lrng.binding.html
import org.scalajs.dom.document
import org.scalajs.dom.raw.Node
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("App")
object App {

  import Routing._

  @html def app: Binding[Node] =
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
          {Menu(createSidebar = true).bind}
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
    html.render(document.body, app)

}
