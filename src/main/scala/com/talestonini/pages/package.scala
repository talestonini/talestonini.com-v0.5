package com.talestonini

import com.thoughtworks.binding.Binding.Var

package object pages {

  // a binding post
  case class BPost(
    docName: Var[String],
    title: Var[String],
    resource: Var[String],
    publishDate: Var[String]
  )

  // a binding comment
  case class BComment(
    author: Var[String],
    text: Var[String],
    date: Var[String]
  )

}
