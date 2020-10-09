package com.talestonini

import com.talestonini.Routing._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.BindingSeq
import com.thoughtworks.binding.Binding.Var
import components.{Footer, Logo, Menu}
import firebase._
import org.lrng.binding.html
import org.scalajs.dom.document
import org.scalajs.dom.raw.{Event, Node}
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, JSGlobal}

@JSExportTopLevel("App")
object App {

  @js.native
  @JSGlobal("uiConfig")
  val uiConfig: Object = js.native

  @js.native
  @JSGlobal("uiStart")
  def uiStart(component: String, uiConfig: Object): Unit = js.native

  @js.native
  @JSGlobal("handleSignedInUserJS")
  def handleSignedInUserJS(): Unit = js.native

  @js.native
  @JSGlobal("handleSignedOutUserJS")
  def handleSignedOutUserJS(): Unit = js.native

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
      (userInfo: UserInfo) =>
        if (Option(userInfo).isDefined)
          handleSignedInUser(userInfo)
        else
          handleSignedOutUser(),
      (err: firebase.auth.Error) => println("error on auth state changed"),
      () => {}
    )

  def handleSignedInUser(userInfo: UserInfo) = {
    user.isLoggedIn.value = true
    user.displayName.value = userInfo.displayName.toString
    user.email.value = userInfo.email.toString
    user.providerId.value = userInfo.providerId
    user.uid.value = userInfo.uid
    handleSignedInUserJS()
  }

  def handleSignedOutUser() = {
    user.isLoggedIn.value = false
    user.displayName.value = ""
    user.email.value = ""
    user.providerId.value = ""
    user.uid.value = ""
    handleSignedOutUserJS()
    uiStart("#firebaseui-auth-container", uiConfig)
  }

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

  @html def appContent(): Binding[BindingSeq[Node]] = Binding {
    <div id="user-signed-in" class="hidden">
      <p>Hello, {user.displayName.bind}!</p>
      <button onclick={e: Event => Firebase.auth().signOut()}>Sign Out</button>
      <div class="content">{route.state.bind.content.value.bind}</div>
    </div>
    <div id="user-signed-out" class="hidden">
      <p>Hello!</p>
      <div id="firebaseui-auth-container"></div>
    </div>
  }

  @JSExport("main")
  def main(): Unit =
    html.render(document.body, app)

}
