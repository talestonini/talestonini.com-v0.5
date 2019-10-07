package com.talestonini.pages

import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.{Binding, dom}


object About {

  @dom
  def apply(): Binding[Node] =
    <div>
      <p>Tales is a data engineer at Eliiza.</p>
    </div>
  
}

