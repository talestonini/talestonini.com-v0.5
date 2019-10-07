package com.talestonini.components

import org.scalajs.{dom => jsdom}
import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.Binding.Vars
import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.BindingSeq

case class MenuItem(label: String, href: String)

object Menu {

  val menuItems = Vars(
    MenuItem("Item 1", "#"),
    MenuItem("Item 2", "#"),
    MenuItem("Item 3", "#"),
    MenuItem("Item 4", "#")
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
            <a href={mi.href} class={normalClasses}>{mi.label}</a>
          }
        }
        <a href="#" class={hamburgerClasses} data:onclick="w3_toggle_open_close()">☰</a>
      </div>
    </div>
    <div class="w3-sidebar w3-bar-block mobile-menu" style="display:none" id="mySidebar">
      {
        for (mi <- menuItems) yield {
          <a href={mi.href} class={mobileClasses}>{mi.label}</a>
        }
      }
    </div>
  }

}

