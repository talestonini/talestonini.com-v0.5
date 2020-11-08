package com.talestonini

import com.talestonini.Routing._
import com.talestonini.utils.js.display
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

  case object user extends SimpleObservable {
    val displayName         = Var("")
    val email               = Var("")
    val providerId          = Var("")
    val uid                 = Var("")
    var accessToken: String = _
  }

  // TODO: this is not needed for the app, but at the moment is needed for tests to run - investigate
  Firebase.initializeApp(FirebaseConfig(), "[DEFAULT]")

  Firebase
    .auth()
    .onAuthStateChanged(
      (userInfo: User) => {
        if (!getFromStorage(userClickedSignOut)) {
          displayLoadingUserInfo()
        }
        if (Option(userInfo).isDefined) {
          captureUserInfo(userInfo)
          hideSignInProviders()
          hideLoadingUserInfo()
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

  @JSExport("main")
  def main(): Unit =
    html.render(document.body, app())

  // -------------------------------------------------------------------------------------------------------------------

  // local storage keys
  private val userClickedSignOut = "userClickedSignOut"

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
    val notNowClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black not-now"
    <div>
      <div id="loding-user-info" class="hidden sign-in-providers"
        style={s"display:${display(loadingUserInfo.bind)}"}>
        Loading user info...
      </div>
      <div id="sign-in-providers" class="hidden sign-in-providers"
        style={s"display:${display(signInProviders.bind)}"}>
        <div id="firebaseui-auth-container"></div>
        <a class={notNowClasses} onclick={e: Event => hideSignInProviders()}>(Not now)</a>
      </div>
      <div class="content">{route.state.bind.content.value.bind}</div>
    </div>
  }

  private val loadingUserInfo                = Var(false)
  private def displayLoadingUserInfo(): Unit = loadingUserInfo.value = true
  private def hideLoadingUserInfo(): Unit    = loadingUserInfo.value = false

  private val signInProviders          = Var(false)
  private def displaySignInProviders() = signInProviders.value = true
  private def hideSignInProviders()    = signInProviders.value = false

  private def captureUserInfo(userInfo: User, printAccessToken: Boolean = true): Unit = {
    def anyToStr(any: Any): String = if (any != null) any.toString else ""

    user.displayName.value = anyToStr(userInfo.displayName)
    user.email.value = anyToStr(userInfo.email)
    user.providerId.value = userInfo.providerId
    user.uid.value = userInfo.uid
    userInfo
      .getIdToken()
      .then(
        (accessToken: Any) => {
          user.accessToken = accessToken.toString
          if (printAccessToken)
            println(user.accessToken)
        },
        (err: Error) => println("error getting access token")
      )
  }

  private def discardUserInfo(): Unit = {
    user.displayName.value = ""
    user.email.value = ""
    user.providerId.value = ""
    user.uid.value = ""
  }

  private def setInStorage(key: String, value: Boolean) =
    LocalStorage.update(key, value.toString)

  private def getFromStorage(key: String) =
    LocalStorage.apply(key).map(_.toBoolean).getOrElse(false)

  @js.native
  @JSGlobal("uiStart")
  private def uiStart(): Unit = js.native

}
