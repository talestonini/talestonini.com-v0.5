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

object UrbanForestChallenge extends BasePostPage {

  @html def postContent(): NodeBinding[HTMLDivElement] =
    <div>
      <p>When I started at <a href="https://eliiza.com.au">Eliiza</a> in September 2018, I was the first <em>data engineer</em> of the team,
      among a few machine learning engineers, data scientists, our CTO and CEO.  We were only 7 people in total then and
      wanted to grow.</p>
      <p>This is the first code challenge that I developed to recruit other data engineers like myself, i.e. <em>software engineers</em>
      that appreciate solving Big Data problems, distributed systems and parallel programming.  As such, it requires the use
      of <a href="https://spark.apache.org">Apache Spark</a> to determine the <strong>greenest suburb of Melbourne</strong>.</p>
      <p>Check it out <a href="https://github.com/eliiza/challenge-urban-forest">here</a>!  Would you give it a crack?</p>
    </div>

  def postPreviewContent(): Binding[BindingSeq[Node]] = Binding {
    postContent().value.childNodes(1)
  }

}
