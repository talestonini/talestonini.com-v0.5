package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Login {

  @html def apply(): Binding[Node] =
    <div>
      <div id="firebaseui-auth-container"></div>
    </div>

}
