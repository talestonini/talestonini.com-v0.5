package com.talestonini.components

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{BindingSeq, Vars, Var}
import org.lrng.binding.html
import org.scalajs.dom.raw.{Event, Node}

object Menu {

  case class MenuItem(labels: Array[String], label: Var[String], hash: String)

  val menuItems: Vars[MenuItem] = Vars(
    MenuItem(Array("Posts", "Artigos"), Var("Posts"), "#/posts"),
    MenuItem(Array("Tags", "Tags"), Var("Tags"), "#/tags"),
    MenuItem(Array("About", "Sobre"), Var("About"), "#/about")
  )

  @html private def changeLang(lang: Int) =
    for (mi <- menuItems.value) yield mi.label.value = mi.labels(lang)

  @html def apply(createSidebar: Boolean = false): Binding[BindingSeq[Node]] = Binding {
    val normalClasses     = """w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-hide-small
                              | menu-item""".stripMargin
    val langClasses       = """w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-white w3-hide-small
                              | lang-menu-item""".stripMargin
    val pipeClasses       = """w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-white w3-hide-small
                              | pipe""".stripMargin
    val mobileLangClasses = "w3-button mobile-lang-menu-item w3-large"
    val sid               = if (createSidebar) "sidebar" else ""

    <div class="w3-col w3-right w3-hide-small" style="width:180px">
      <div class="menu menu-lang">
        <p class={pipeClasses}>|</p>
        <a class={langClasses} onclick={e: Event => changeLang(0)}>English</a>
        <a class={langClasses} onclick={e: Event => changeLang(1)}>Português</a>
      </div>
    </div>
    <div class="w3-rest w3-hide-small">
      <div class="menu">
        {
      for (mi <- menuItems) yield <a href={mi.hash} class={normalClasses}>{mi.label.bind}</a>
    }
      </div>
    </div>

    <div class="w3-rest w3-hide-large w3-hide-medium">
      <div class="menu menu-lang">
        <a class={mobileLangClasses} onclick={e: Event => changeLang(0)}>En</a>
        <a class={mobileLangClasses} onclick={e: Event => changeLang(1)}>Pt</a>
        <a class="w3-button w3-xxxlarge hamburger" data:onclick="toggle_sidebar()">☰</a>
      </div>
    </div>
    <div class="w3-sidebar w3-bar-block mobile-menu" style="display:none" id={sid}>
      {
      for (mi <- menuItems) yield <a href={mi.hash} class="w3-bar-item w3-button" data:onclick="toggle_sidebar()">{
        mi.label.bind
      }</a>
    }
    </div>
  }

}
