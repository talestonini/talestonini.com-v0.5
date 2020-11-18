package com.talestonini.pages.posts

import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object FunProgCapstone extends BasePostPage {

  @html def apply(): Binding[Node] = body()

  @html def postContent(): Binding[Node] = <p>It&#39;s very easy to make some words <strong>bold</strong> and other words <em>italic</em> with Markdown. You can even <a href="http://google.com">link to Google!</a></p>

}
