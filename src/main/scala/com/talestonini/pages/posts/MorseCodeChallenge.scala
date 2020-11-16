package com.talestonini.pages.posts

import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object MorseCodeChallenge extends BasePostPage {

  @html def apply(): Binding[Node] = body()

  @html def content(): Binding[Node] = <p>This is an <strong>Apache Kafka</strong>-based code challenge.</p>

}
