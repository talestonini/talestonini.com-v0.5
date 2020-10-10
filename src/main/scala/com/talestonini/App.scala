package com.talestonini

import com.talestonini.Routing._
import com.thoughtworks.binding.Binding
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
  @JSGlobal("greetSignedInUser")
  def greetSignedInUser(): Unit = js.native

  @js.native
  @JSGlobal("greetSignedOutUser")
  def greetSignedOutUser(): Unit = js.native

  @js.native
  @JSGlobal("displaySignInProviders")
  def displaySignInProviders(): Unit = js.native

  @js.native
  @JSGlobal("hideSignInProviders")
  def hideSignInProviders(): Unit = js.native

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
          captureUserInfo(userInfo)
          greetSignedInUser()
          hideSignInProviders()
        } else {
          discardUserInfo()
          greetSignedOutUser()
          uiStart("#firebaseui-auth-container", uiConfig)
        }
      },
      (err: firebase.auth.Error) => println("error on auth state changed"),
      () => {}
    )

  def captureUserInfo(userInfo: UserInfo) = {
    user.isLoggedIn.value = true
    user.displayName.value = userInfo.displayName.toString
    user.email.value = userInfo.email.toString
    user.providerId.value = userInfo.providerId
    user.uid.value = userInfo.uid
  }

  def discardUserInfo() = {
    user.isLoggedIn.value = false
    user.displayName.value = ""
    user.email.value = ""
    user.providerId.value = ""
    user.uid.value = ""
  }

  def handleClickSignIn() = {
    displaySignInProviders()
  }

  def handleClickSignOut() = {
    Firebase.auth().signOut()
  }

  @html def app(): Binding[Node] =
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

  @html def appContent(): Binding[Node] =
    <div>
      <div id="sign-in-providers" class="hidden" style="display: none">
        <div id="firebaseui-auth-container"></div>
      </div>
      <div class="content">{route.state.bind.content.value.bind}</div>
    </div>

  @JSExport("main")
  def main(): Unit =
    html.render(document.body, app())

}
