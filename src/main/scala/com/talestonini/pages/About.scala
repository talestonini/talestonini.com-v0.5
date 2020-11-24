package com.talestonini.pages

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object About {

  @html def apply(): Binding[Node] =
    <div>
      <p>Tales is a <i>Senior Software Engineer</i> working in Data Engineering at
        <a href="https://eliiza.com.au/" target="_blank">Eliiza</a>, in Melbourne, Australia.
      </p>
    </div>

}
