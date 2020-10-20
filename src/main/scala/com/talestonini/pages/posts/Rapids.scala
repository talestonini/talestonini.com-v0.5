package com.talestonini.pages.posts

import com.talestonini.pages.PostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Rapids extends PostPage {

  @html def apply(): Binding[Node] =
    <div>
      <p>Rapids</p>

      {comments()}
    </div>

}
