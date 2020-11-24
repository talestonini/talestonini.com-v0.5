package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

// NOTE: not in use at the moment as the home page is pointing to the about page.
object Home {

  @html def apply(): Binding[Node] =
    <div>
      <p>This is the home content.</p>
    </div>

}
