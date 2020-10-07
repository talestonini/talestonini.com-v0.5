package com.talestonini

import com.thoughtworks.binding.Binding.Var

package object pages {

  /**
    * A binding comment.
    */
  case class BComment(
    author: Var[String],
    text: Var[String],
    date: Var[String]
  )

}
