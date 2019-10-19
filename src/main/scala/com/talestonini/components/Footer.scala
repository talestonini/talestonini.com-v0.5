package com.talestonini.components

import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.{Binding, dom}
import Binding.BindingSeq


object Footer {

  @dom
  def apply(): Binding[BindingSeq[Node]] =
    <div class="w3-xlarge">
      <a href="https://twitter.com/talestonini" class="no-decoration" target="_blank">
        <i class="fa fa-twitter w3-hover-opacity"></i>
      </a>
      <a href="https://au.linkedin.com/in/talestonini" class="no-decoration" target="_blank">
        <i class="fa fa-linkedin w3-hover-opacity"></i>
      </a>
      <a href="https://github.com/talestonini" class="no-decoration" target="_blank">
        <i class="fa fa-github w3-hover-opacity"></i>
      </a>
      <a href="https://www.instagram.com/talestonini" class="no-decoration" target="_blank">
        <i class="fa fa-instagram w3-hover-opacity"></i>
      </a>
      <a href="mailto:talestonini@gmail.com" class="no-decoration" target="_blank">
        <i class="fa fa-envelope w3-hover-opacity"></i>
      </a>
    </div>
    <div class="w3-small">
      <p>© 2019 Tales Tonini</p>
    </div>

}

