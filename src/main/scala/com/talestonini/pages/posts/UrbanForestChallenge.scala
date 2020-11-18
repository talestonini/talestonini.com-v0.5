package com.talestonini.pages.posts

import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object UrbanForestChallenge extends BasePostPage {

  @html def postContent(): Binding[Node] = <p>This is an <strong>Apache Spark</strong>-based code challenge.</p>

}
