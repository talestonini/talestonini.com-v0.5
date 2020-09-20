package com.talestonini

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.Node

package object posts {

  val Posts: Map[String, Binding[Node]] = Map(
    "capstone" -> new Capstone().apply(),
    "rapids"   -> Rapids()
  )

}
