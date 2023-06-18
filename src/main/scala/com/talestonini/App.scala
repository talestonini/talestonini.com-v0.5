package com.talestonini

import com.talestonini.Routing._
import com.talestonini.utils.javascript._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import components.{Footer, Logo, Menu}
import org.lrng.binding.html
import org.scalajs.dom.document
import org.scalajs.dom.raw.Node
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("App")
object App {

  // --- UI ------------------------------------------------------------------------------------------------------------

  @html private def app(): Binding[Node] =
    <div>
      <div class="w3-content w3-row w3-hide-small">
        <div class="w3-padding-16">
          {Logo().bind}
          {Menu().bind}
        </div>
        <hr />
      </div>
      <div class="w3-content w3-row w3-hide-large w3-hide-medium">
        <div class="w3-padding-8">
          {Logo().bind}
          {Menu(isMobile = true).bind}
        </div>
        <hr />
      </div>

      <div class="w3-content">
        <div class="content w3-padding-16">
          <div class="w3-center" style={s"display: ${display(isLoading.bind)}"}>
            <p><i class="w3-xxxlarge fa fa-spinner w3-spin" /></p>
          </div>
          {route.state.bind.content.value.bind}
        </div>
        <hr />
      </div>

      <footer class="w3-container w3-padding-16 w3-center w3-hide-small">
        {Footer().bind}
      </footer>
      <footer class="w3-container w3-padding-16 w3-center w3-hide-large w3-hide-medium">
        {Footer().bind}
      </footer>
    </div>

  @JSExport("main")
  def main(args: Array[String]): Unit =
    html.render(document.body, app())

  // --- public --------------------------------------------------------------------------------------------------------

  // whether the app is loading some content or not;
  // this is the state that backs displaying a loading animation of some kind on the UI
  val isLoading = Var(false)

}
