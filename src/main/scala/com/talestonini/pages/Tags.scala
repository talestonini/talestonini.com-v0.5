package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Tags {

  @html def apply(): Binding[Node] =
    <div>
      <p>Coming soon...</p>
    </div>

}
