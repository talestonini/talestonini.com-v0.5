package com.talestonini.pages.posts

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Rapids extends BasePostPage {

  @html def apply(): Binding[Node] = body()

  @html def content(): Binding[Node] = <p>...</p>

}
