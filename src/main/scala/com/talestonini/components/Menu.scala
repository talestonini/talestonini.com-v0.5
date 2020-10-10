package com.talestonini.components

import com.talestonini.App.{user, handleClickSignIn, handleClickSignOut}
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{BindingSeq, Vars, Var}
import org.lrng.binding.html
import org.scalajs.dom.raw.{Event, Node}

object Menu {

  // CSS classes
  private val commonClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-hide-small"
  val langClasses           = s"$commonClasses lang-menu-item"

  case class MenuItem(label: String, hash: String)

  val menuItems: Seq[MenuItem] = Seq(
    MenuItem("Posts", "#/posts"),
    MenuItem("Tags", "#/tags"),
    MenuItem("About", "#/about")
  )

  @html def apply(createSidebar: Boolean = false): Binding[BindingSeq[Node]] = Binding {
    val pipeClasses       = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hide-small pipe"
    val mobileLangClasses = "w3-button mobile-lang-menu-item w3-large"
    val sid               = if (createSidebar) "sidebar" else ""

    <div class="w3-col w3-right w3-hide-small" style="width:100px">
      <div class="menu menu-lang">
        <p class={pipeClasses}>|</p>
        {greeting()}
      </div>
    </div>
    <div class="w3-rest w3-hide-small">
      <div class="menu">{menu()}</div>
    </div>

    <div class="w3-rest w3-hide-large w3-hide-medium">
      <div class="menu menu-lang">
        <a class="w3-button w3-xxxlarge hamburger" data:onclick="toggle_sidebar()">☰</a>
      </div>
    </div>
    <div class="w3-sidebar w3-bar-block mobile-menu" style="display:none" id={sid}>{mobileMenu()}</div>
  }

  @html private def menu() =
    for (mi <- menuItems)
      yield <a href={mi.hash} class={s"$commonClasses menu-item"}>{mi.label}</a>

  @html private def mobileMenu() =
    for (mi <- menuItems)
      yield <a href={mi.hash} class="w3-bar-item w3-button" data:onclick="toggle_sidebar()">{mi.label}</a>

  @html private def greeting(): Binding[Node] =
    <div>
      <div id="greeting-signed-in" class="hidden greeting" style="display: none">
        <p>Hi, {user.displayName.bind}!</p>
        <a class={langClasses} onclick={e: Event => handleClickSignOut()}>(Sign out)</a>
      </div>
      <div id="greeting-signed-out" class="hidden greeting" style="display: none">
        <p>Hi!</p>
        <a class={langClasses} onclick={e: Event => handleClickSignIn()}>(Sign in)</a>
      </div>
    </div>

}
