package com.talestonini.components

import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.Binding.Vars
import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.BindingSeq


object Menu {

  case class MenuItem(label: String, hash: String)

  val menuItems = Vars(
    MenuItem("Item 1", "#/itemOne"),
    MenuItem("Item 2", "#/itemTwo"),
    MenuItem("About", "#/about")
  )
  
  @dom
  def apply(): Binding[BindingSeq[Node]] = {
    val normalClasses = "w3-button w3-hover-none w3-border-white w3-bottombar w3-hover-border-black w3-padding-10 w3-hide-small"
    val hamburgerClasses = "w3-button w3-padding-10 w3-xxlarge w3-hide-large w3-hide-medium"
    val mobileClasses = "w3-bar-item w3-button"
    <div class="w3-rest">
      <div class="menu">
        {
          for (mi <- menuItems) yield {
            <a href={mi.hash} class={normalClasses}>{mi.label}</a>
          }
        }
        <a class={hamburgerClasses} data:onclick="toggle_sidebar()">☰</a>
      </div>
    </div>
    <div class="w3-sidebar w3-bar-block mobile-menu" style="display:none" id="sidebar">
      {
        for (mi <- menuItems) yield {
          <a href={mi.hash} class={mobileClasses} data:onclick="toggle_sidebar()">{mi.label}</a>
        }
      }
    </div>
  }

}

