package com.talestonini.components

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Menu {

  private case class MenuItem(label: String, hash: String)

  private val menuItems: Seq[MenuItem] = Seq(
    MenuItem("Posts", "#/posts"),
    MenuItem("Tags", "#/tags"),
    MenuItem("About", "#/about")
  )

  @html def apply(isMobile: Boolean = false): Binding[Binding[Node]] = Binding {
    val menuElems: Binding[Node] =
      <div class="w3-rest w3-hide-small">
        <div class="menu">{menu()}</div>
      </div>

    val mobileMenuElems: Binding[Node] =
      <div>
        <div class="w3-hide-large w3-hide-medium">
          <div class="hamburger">
            <a class="w3-button w3-xxxlarge fa fa-bars" data:onclick="toggleSidebar()" />
          </div>
        </div>
        <div id="sidebar" class="w3-sidebar w3-bar-block w3-animate-top mobile-menu"
          style="display: none; padding-top: 8px">
          {mobileMenu()}
        </div>
        <div id="overlay" class="w3-overlay" data:onclick="toggleSidebar()" style="cursor: pointer" />
      </div>

    if (!isMobile) menuElems else mobileMenuElems
  }

  @html private def menu() =
    for (mi <- menuItems)
      yield <a href={mi.hash} class={s"$commonClasses menu-item"}>{mi.label}</a>

  @html private def mobileMenu() = {
    val commonClasses = "w3-bar-item w3-button w3-bold"

    val close =
      <a class={s"$commonClasses w3-xxxlarge w3-right-align fa fa-close"} style="padding-bottom: 23px"
        data:onclick="toggleSidebar()" />

    def w3Color(n: Int): String = if (n % 2 == 0) "w3-white" else "w3-light-grey"
    val items =
      for ((mi, i) <- menuItems.zipWithIndex)
        yield <a href={mi.hash} class={s"$commonClasses w3-xlarge ${w3Color(i)}"} data:onclick="toggleSidebar()">
                {mi.label}
              </a>

    close +: items
  }

  private val commonClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-hide-small"

}
