package com.talestonini.pages

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import org.lrng.binding.html
import org.scalajs.dom.raw.Node
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import firebase.FirebaseConfig
import firebase.Firebase
import firebase.app._
import firebase.auth._
import firebase.UserInfo
import firebase.User

object LoggedIn {

  @js.native
  @JSGlobal("getSomeData")
  def getSomeData(): String = js.native

  def getUserData(): Var[String] = {
    println("getting user data...")
    val res: Var[String] = Var("...")
    Firebase
      .auth()
      .onAuthStateChanged(
        (user: UserInfo) => res.value = user.email.toString,
        (err: firebase.auth.Error) => res.value = "no user",
        () => {}
      )
    res
  }

  @html def apply(): Binding[Node] = {
    <div>
      <div id="sign-in-status"></div>
      <div id="sign-in"></div>
      <pre id="account-details"></pre>

      <p>{getUserData().bind}</p>

      <p><a href="#/">Home</a></p>
    </div>
  }

}
