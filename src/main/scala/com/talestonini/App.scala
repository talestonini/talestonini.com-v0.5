package com.talestonini

import com.talestonini.Routing._
import com.talestonini.utils.js.display
import com.talestonini.utils.observer.SimpleObservable
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

  case object user extends SimpleObservable {
    var isLoggedIn: Boolean = false
    val displayName         = Var("")
    val email               = Var("")
    val providerId          = Var("")
    val uid                 = Var("")
    var accessToken: String = _
  }

  Firebase
    .auth()
    .onAuthStateChanged(
      (userInfo: User) => {
        if (Option(userInfo).isDefined) {
          captureUserInfo(userInfo)
          hideSignInProviders()
        } else {
          discardUserInfo()
          uiStart()
        }
      },
      (err: firebase.auth.Error) => println("error on auth state changed"),
      () => {}
    )

  def handleClickSignIn(): Unit = displaySignInProviders()

  def handleClickSignOut(): Unit = Firebase.auth().signOut()

  @JSExport("main")
  def main(): Unit =
    html.render(document.body, app())

  // -------------------------------------------------------------------------------------------------------------------

  @html private def app(): Binding[Node] =
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
          {Menu(isMobile = true).bind}
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

  @html private def appContent(): Binding[Node] = {
    val noThanksClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black no-thanks"
    <div>
      <div id="sign-in-providers" class="hidden sign-in-providers"
        style={s"display:${display(isDisplaySignInProviders.bind)}"}>
        <div id="firebaseui-auth-container"></div>
        <a class={noThanksClasses} onclick={e: Event => hideSignInProviders()}>(no, thanks)</a>
      </div>
      <div class="content">{route.state.bind.content.value.bind}</div>
    </div>
  }

  private val isDisplaySignInProviders = Var(false)
  private def displaySignInProviders() = isDisplaySignInProviders.value = true
  private def hideSignInProviders()    = isDisplaySignInProviders.value = false

  private def captureUserInfo(userInfo: User): Unit = {
    def firstStr(any: Any): String =
      if (any != null) any.toString.split(" ")(0)
      else ""

    user.isLoggedIn = true
    user.displayName.value = firstStr(userInfo.displayName)
    user.email.value = firstStr(userInfo.email)
    user.providerId.value = userInfo.providerId
    user.uid.value = userInfo.uid
    userInfo
      .getIdToken()
      .then(
        (accessToken: Any) => {
          user.accessToken = accessToken.toString
          user.notifyObservers("UserSignedIn")
        },
        (err: Error) => println("error getting access token")
      )
  }

  private def discardUserInfo(): Unit = {
    user.isLoggedIn = false
    user.displayName.value = ""
    user.email.value = ""
    user.providerId.value = ""
    user.uid.value = ""
    user.notifyObservers("UserSignedOut")
  }

  @js.native
  @JSGlobal("uiStart")
  private def uiStart(): Unit = js.native

}
