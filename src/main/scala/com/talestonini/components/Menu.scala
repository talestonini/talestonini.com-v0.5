package com.talestonini.components

import org.scalajs.dom.raw.Node
import org.scalajs.dom.raw.Event

import com.thoughtworks.binding.Binding.{Vars, Var}
import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.BindingSeq


object Menu {

  case class MenuItem(labels: Array[String], label: Var[String], hash: String)

  val menuItems: Vars[MenuItem] = Vars(
    MenuItem(Array("Posts", "Artigos"), Var("Posts"), "#/itemOne"),
    MenuItem(Array("Tags", "Tags"), Var("Tags"), "#/itemTwo"),
    MenuItem(Array("About", "Sobre"), Var("About"), "#/about")
  )

  @dom
  def changeLang(lang: Int) =
    for (mi <- menuItems.value) yield
      mi.label.value = mi.labels(lang)

  @dom
  def apply(): Binding[BindingSeq[Node]] = {
    val normalClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-padding-10 w3-hide-small menu-item"
    val langClasses   = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-padding-10 w3-hide-small lang-menu-item"
    val pipeClasses   = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-white w3-padding-10 w3-hide-small pipe"
    val mobileLangClasses = "w3-button mobile-lang-menu-item w3-large"

    <div class="w3-col w3-right w3-hide-small" style="width:180px">
      <div class="menu menu-lang">
        <p class={pipeClasses}>|</p>
        <a class={langClasses} onclick={ e: Event => changeLang(0) }>English</a>
        <a class={langClasses} onclick={ e: Event => changeLang(1) }>Português</a>
      </div>
    </div>
    <div class="w3-rest w3-hide-small">
      <div class="menu">
        {
          for (mi <- menuItems) yield
            <a href={mi.hash} class={normalClasses}>{mi.label.bind}</a>
        }
      </div>
    </div>

    <div class="w3-rest w3-hide-large w3-hide-medium">
      <div class="menu menu-lang">
        <a class={mobileLangClasses} onclick={ e: Event => changeLang(0) }>En</a>
        <a class={mobileLangClasses} onclick={ e: Event => changeLang(1) }>Pt</a>
        <a class="w3-button w3-padding-10 w3-xxlarge" data:onclick="toggle_sidebar()">☰</a>
      </div>
    </div>
    <div class="w3-sidebar w3-bar-block mobile-menu" style="display:none" id="sidebar">
      {
        for (mi <- menuItems) yield
          <a href={mi.hash} class="w3-bar-item w3-button" data:onclick="toggle_sidebar()">{mi.label.bind}</a>
      }
    </div>
  }

}

