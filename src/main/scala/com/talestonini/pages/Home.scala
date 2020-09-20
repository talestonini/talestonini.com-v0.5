package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Home {

  @html def apply(): Binding[Node] =
    <div>
      <p>Home page content...</p>
    </div>

}
