package com.talestonini.components

import org.scalajs.{dom => jsdom}
import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.{Binding, dom}

object Footer {

  @dom
  def apply(): Binding[Node] =
    <footer class="footer">
      <p>Footer</p>
    </footer>

}

