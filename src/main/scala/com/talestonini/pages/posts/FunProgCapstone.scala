package com.talestonini.pages.posts

import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.BindingSeq
import com.thoughtworks.binding.Binding.Var
import org.lrng.binding.html
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.HTMLDivElement
import org.scalajs.dom.raw.{Node, NodeList}

object FunProgCapstone extends BasePostPage {

  @html def postContent(): NodeBinding[HTMLDivElement] =
    <div>
      <p>It&#39;s very easy to make some words <strong>bold</strong> and other words <em>italic</em> with Markdown. You can even <a href="http://google.com">link to Google!</a></p>
    </div>

  def postPreviewContent(): Binding[BindingSeq[Node]] = Binding {
    postContent().value.childNodes(1)
  }

}
