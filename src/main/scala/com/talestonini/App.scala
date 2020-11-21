package com.talestonini

import com.talestonini.Routing._
import com.talestonini.utils.javascript._
import com.talestonini.utils.observer.EventName._
import com.talestonini.utils.observer.SimpleObservable
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import components.{Footer, Logo, Menu}
import firebase._
import org.lrng.binding.html
import org.scalajs.dom.document
import org.scalajs.dom.ext.LocalStorage
import org.scalajs.dom.raw.{Event, Node}
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, JSGlobal}

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
        <div id="sign-in-providers w3-padding-16" class="hidden sign-in-providers"
          style={s"display:${display(isSignInProvidersVisible.bind)}"}>
          <div id="firebaseui-auth-container"></div>
          <div class="not-now">
            <a class="menu-item w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black"
              onclick={e: Event => hideSignInProviders()}>(Not now)</a>
          </div>
          <hr></hr>
        </div>
        <div class="content w3-padding-16">
          <div class="w3-center" style={s"display:${display(isLoading.bind)}"}>
            <p><i class="fa fa-spinner w3-spin" style="font-size:50px"></i></p>
          </div>
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
  def main(): Unit = html.render(document.body, app())

  // Firebase UI (auth stuff)
  @js.native
  @JSGlobal("uiStart")
  private def uiStart(): Unit = js.native

  // --- public --------------------------------------------------------------------------------------------------------

  // signed in user info
  case object user extends SimpleObservable {
    val displayName         = Var("")
    val email               = Var("")
    val providerId          = Var("")
    val uid                 = Var("")
    var accessToken: String = _
  }

  // react to user signing in/out
  Firebase
    .auth()
    .onAuthStateChanged(
      (userInfo: User) => {
        if (!getFromStorage(userClickedSignOut))
          displayLoading()

        if (Option(userInfo).isDefined) {
          captureUserInfo(userInfo)
          hideSignInProviders()
          hideLoading()
          user.notifyObservers(UserSignedIn)
        } else {
          discardUserInfo()
          uiStart()
          user.notifyObservers(UserSignedOut)
        }
      },
      (err: firebase.auth.Error) => println("error capturing auth state change"),
      () => {}
    )

  def handleClickSignIn(): Unit = {
    setInStorage(userClickedSignOut, false)
    displaySignInProviders()
  }

  def handleClickSignOut(): Unit = {
    setInStorage(userClickedSignOut, true)
    Firebase.auth().signOut()
  }

  // --- private -------------------------------------------------------------------------------------------------------

  // local storage keys
  private val userClickedSignOut = "userClickedSignOut"

  private val isLoading = Var(false)
  def displayLoading()  = isLoading.value = true
  def hideLoading()     = isLoading.value = false

  private val isSignInProvidersVisible = Var(false)
  private def displaySignInProviders() = isSignInProvidersVisible.value = true
  private def hideSignInProviders()    = isSignInProvidersVisible.value = false

  private def captureUserInfo(userInfo: User): Unit = {
    def anyToStr(any: Any): String = if (any != null) any.toString else ""

    user.displayName.value = anyToStr(userInfo.displayName)
    user.email.value = anyToStr(userInfo.email)
    user.providerId.value = userInfo.providerId
    user.uid.value = userInfo.uid
    userInfo
      .getIdToken()
      .then(
        (accessToken: Any) => user.accessToken = accessToken.toString,
        (err: Error) => println("error getting access token")
      )
  }

  private def discardUserInfo(): Unit = {
    user.displayName.value = ""
    user.email.value = ""
    user.providerId.value = ""
    user.uid.value = ""
  }

  // browser local storage use
  private def setInStorage(key: String, value: Boolean) = LocalStorage.update(key, value.toString)
  private def getFromStorage(key: String)               = LocalStorage.apply(key).map(_.toBoolean).getOrElse(false)

}
