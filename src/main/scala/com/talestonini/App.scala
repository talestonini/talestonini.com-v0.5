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

  trait Observer {
    def onNotify(event: String): Unit
  }

  trait Observable {
    def observe(observer: Observer, event: String): Unit
    def notifyObservers(event: String): Unit
  }

  case object user extends Observable {
    var isLoggedIn: Boolean = false
    val displayName         = Var("")
    val email               = Var("")
    val providerId          = Var("")
    val uid                 = Var("")
    var accessToken: String = _

    var observers: Map[String, Seq[Observer]] = Map.empty

    def observe(observer: Observer, event: String): Unit = {
      var eventObservers = observers.get(event).getOrElse(Seq.empty)
      if (eventObservers.isEmpty)
        observers = observers + (event -> (eventObservers :+ observer))
      else
        eventObservers = eventObservers :+ observer
    }

    def notifyObservers(event: String): Unit = {
      val eventObservers = observers.get(event).getOrElse(Seq.empty)
      eventObservers.foreach(_.onNotify(event))
    }
  }

  Firebase
    .auth()
    .onAuthStateChanged(
      (userInfo: User) => {
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

  def captureUserInfo(userInfo: User) = {
    user.isLoggedIn = true
    user.displayName.value = userInfo.displayName.toString
    user.email.value = userInfo.email.toString
    user.providerId.value = userInfo.providerId
    user.uid.value = userInfo.uid
    userInfo
      .getIdToken()
      .then(
        (accessToken: Any) => {
          println(accessToken)
          user.accessToken = accessToken.toString
          user.notifyObservers("userLoggedIn")
        },
        (err: Error) => println("error getting access token")
      )
  }

  def discardUserInfo() = {
    user.isLoggedIn = false
    user.displayName.value = ""
    user.email.value = ""
    user.providerId.value = ""
    user.uid.value = ""
    user.notifyObservers("userLoggedOut")
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

  @html def appContent(): Binding[Node] = {
    val noThanksClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black no-thanks"
    <div>
      <div id="sign-in-providers" class="hidden sign-in-providers" style="display: none">
        <div id="firebaseui-auth-container"></div>
        <a class={noThanksClasses} onclick={e: Event => hideSignInProviders()}>(no, thanks)</a>
      </div>
      <div class="content">{route.state.bind.content.value.bind}</div>
    </div>
  }

  @JSExport("main")
  def main(): Unit =
    html.render(document.body, app())

}
