package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node


object AboutPage {

  @html def apply(): Binding[Node] =
    <div>
      <p>Tales is a data engineer at Eliiza.</p>
    </div>
  
}
