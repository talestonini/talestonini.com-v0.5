package com.talestonini.pages.posts

import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object DockerVim extends BasePostPage {

  @html def postContent(): Binding[Node] =
    <div>
      <p>Hi</p>
    </div>

}
