package com.talestonini.posts

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Rapids {

  @html def apply(): Binding[Node] =
    <div>
      <p>Rapids</p>
    </div>

}
