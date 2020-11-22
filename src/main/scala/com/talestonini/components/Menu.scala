package com.talestonini.components

import com.talestonini.App.{user, handleClickSignIn, handleClickSignOut}
import com.talestonini.utils.javascript.display
import com.talestonini.utils.observer.EventName._
import com.talestonini.utils.observer.Observer
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{BindingSeq, Vars, Var}
import org.lrng.binding.html
import org.scalajs.dom.raw.{Event, Node}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

object Menu extends Observer {

  private case class MenuItem(label: String, hash: String)

  private val menuItems: Seq[MenuItem] = Seq(
    MenuItem("Posts", "#/posts"),
    MenuItem("Tags", "#/tags"),
    MenuItem("About", "#/about")
  )

  @html def apply(isMobile: Boolean = false): Binding[BindingSeq[Node]] = Binding {
    val menuElems = {
      <div class="w3-col w3-right w3-hide-small" style="width: 100px">
        <div class="menu menu-sign-in-out">
          <p class="w3-button w3-hover-none w3-border-white w3-bottombar w3-hide-small pipe">|</p>
          {greetUser()}
        </div>
      </div>
      <div class="w3-rest w3-hide-small">
        <div class="menu">{menu()}</div>
      </div>
    }

    val mobileMenuElems = {
      <div class="w3-rest w3-hide-large w3-hide-medium">
        <div class="menu menu-sign-in-out">
          <a class="w3-button w3-xxxlarge hamburger" data:onclick="toggleSidebar()">☰</a>
        </div>
      </div>
      <div id="sidebar" class="w3-sidebar w3-bar-block w3-animate-right mobile-menu" style="display: none; z-index: 5">
        {mobileMenu()}
      </div>
      <div id="overlay" class="w3-overlay w3-animate-opacity" data:onclick="toggleSidebar()" style="cursor: pointer" />
    }

    if (!isMobile) menuElems else mobileMenuElems
  }

  @html private def greetUser(): Binding[Node] = {
    def firstStr(str: String) = str.split(" ")(0)

    val signInOutClasses = s"$commonClasses menu-item-sign-in-out"
    <div>
      <div id="greet-signed-in" class="greeting hidden" style={s"display: ${display(isUserSignedIn.bind)}"}>
        <p>Hi, {firstStr(user.displayName.bind)}!</p>
        <a class={signInOutClasses} onclick={e: Event => handleClickSignOut()}>(Sign out)</a>
      </div>
      <div id="greet-signed-out" class="greeting hidden" style={s"display: ${display(!isUserSignedIn.bind)}"}>
        <p>Hi!</p>
        <a class={signInOutClasses} onclick={e: Event => handleClickSignIn()}>(Sign in)</a>
      </div>
    </div>
  }

  @html private def menu() =
    for (mi <- menuItems)
      yield <a href={mi.hash} class={s"$commonClasses menu-item"}>{mi.label}</a>

  @html private def mobileMenu() = {
    val signInOutClasses = "w3-bar-item w3-button"

    def onClick(handler: () => Unit) = {
      handler()
      toggleSidebar()
    }

    val signOut =
      <a id="greet-signed-in-mobile" class={signInOutClasses} style={s"display: ${display(isUserSignedIn.bind)}"}
        onclick={e: Event => onClick(handleClickSignOut)}>
        Hi, {user.displayName.bind}! (Sign out)
      </a>

    val signIn =
      <a id="greet-signed-out-mobile" class={signInOutClasses} style={s"display: ${display(!isUserSignedIn.bind)}"}
        onclick={e: Event => onClick(handleClickSignIn)}>
        Hi! (Sign in)
      </a>

    val items =
      for (mi <- menuItems)
        yield <a href={mi.hash} class={signInOutClasses} data:onclick="toggleSidebar()">{mi.label}</a>

    Seq(signIn, signOut) ++ items
  }

  // react to user signing in/out
  private val isUserSignedIn = Var(false)
  user.register(this, UserSignedIn, UserSignedOut)
  def onNotify(e: EventName): Unit = e match {
    case UserSignedIn  => isUserSignedIn.value = true
    case UserSignedOut => isUserSignedIn.value = false
  }

  @js.native
  @JSGlobal("toggleSidebar")
  private def toggleSidebar(): Unit = js.native

  private val commonClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-hide-small"

}
