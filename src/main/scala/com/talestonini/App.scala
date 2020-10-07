package com.talestonini

import com.talestonini.Routing._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import components.{Footer, Logo, Menu}
import firebase._
import org.lrng.binding.html
import org.scalajs.dom.document
import org.scalajs.dom.raw.Node
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("App")
object App {

  case object user {
    val isLoggedIn  = Var(false)
    val displayName = Var("")
    val email       = Var("")
    val providerId  = Var("")
    val uid         = Var("")
  }

  Firebase
    .auth()
    .onAuthStateChanged(
      (userInfo: UserInfo) => {
        if (Option(userInfo).isDefined) {
          user.isLoggedIn.value = true
          user.displayName.value = userInfo.displayName.toString
          user.email.value = userInfo.email.toString
          user.providerId.value = userInfo.providerId
          user.uid.value = userInfo.uid
        } else {
          user.isLoggedIn.value = false
          user.displayName.value = ""
          user.email.value = ""
          user.providerId.value = ""
          user.uid.value = ""
        }
      },
      (err: firebase.auth.Error) => println("user is not logged in"),
      () => {}
    )

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
        {appContent()}
        <hr></hr>
      </div>

      <footer class="w3-container w3-padding-16 w3-center w3-hide-small">
        {Footer().bind}
      </footer>
      <footer class="w3-container w3-padding-8 w3-center w3-hide-large w3-hide-medium">
        {Footer().bind}
      </footer>
    </div>

  @html def appContent(): Binding[Node] = {
    if (user.isLoggedIn.value) route.state.value = hash2Page("login")
    <div class="content">{route.state.bind.content.value.bind}</div>
  }

  @JSExport("main")
  def main(): Unit =
    html.render(document.body, app)

}
