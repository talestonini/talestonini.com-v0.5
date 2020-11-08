package com.talestonini.pages.posts

import com.talestonini.pages.PostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Capstone extends PostPage {

  @html def apply(): Binding[Node] = body()

  def title(): String = "Capstone"

  @html def content(): Binding[Node] = <p>...</p>

}
