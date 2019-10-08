package com.talestonini.components

import org.scalajs.dom.raw.Node

import com.thoughtworks.binding.{Binding, dom}


object Footer {

  @dom
  def apply(): Binding[Node] =
    <footer class="w3-container w3-padding-16 w3-center">
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
      </div>
      <div class="w3-small">
        <p>© Tales Tonini</p>
      </div>
    </footer>

}

