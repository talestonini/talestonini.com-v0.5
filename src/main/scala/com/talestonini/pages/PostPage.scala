package com.talestonini.pages

import com.talestonini.db.Firebase
import com.talestonini.db.model._
import com.talestonini.utils._
import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.Binding.Vars
import org.lrng.binding.html
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

trait PostPage {

  val postRestEntityLinkPromise = Promise[String]()

  val bComments = Vars.empty[BComment]

  //postRestEntityLinkPromise.future
  //.onComplete({
  //case link: Success[String] =>
  //Firebase
  //.getComments(link.get)
  //.onComplete({
  //case comments: Success[Comments] =>
  //for (c <- comments.get)
  //bComments.value += BComment(
  //author = Var(c.fields.author.get),
  //text = Var(c.fields.text.get),
  //date = Var(datetime2Str(c.fields.date))
  //)
  //case f: Failure[Comments] =>
  //println(s"failure getting comments: ${f.exception.getMessage()}")
  //})
  //case f: Failure[String] =>
  //println(s"failure getting postRestEntityLink: ${f.exception.getMessage()}")
  //})

  @html def comments() =
    for (c <- bComments)
      yield <p>{c.text.bind}</p>

}
