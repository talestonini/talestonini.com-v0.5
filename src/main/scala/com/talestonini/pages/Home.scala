package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Home {

  @html def apply(): Binding[Node] =
    <div>
      <div id="firebaseui-auth-container"></div>

      <div id="sign-in-status"></div>
      <div id="sign-in"></div>
      <pre id="account-details"></pre>

      <p>Home page content...</p>
    </div>

}
