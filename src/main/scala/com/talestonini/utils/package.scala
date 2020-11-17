package com.talestonini

import java.time._
import java.time.format.DateTimeFormatter.{ofPattern => pattern}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

package object utils {

  private val SimpleDateFormatter = pattern("dd LLL yyyy")

  def datetime2Str(datetime: ZonedDateTime): String =
    datetime
      .toInstant()
      .atZone(ZoneId.systemDefault())
      .format(SimpleDateFormatter)

  def datetime2Str(datetime: Option[ZonedDateTime], default: String = "no date"): String =
    datetime match {
      case Some(dt) => datetime2Str(dt)
      case None     => default
    }

  def now() = ZonedDateTime.now(ZoneId.of("UTC"))

  def randomAlphaNumericString(length: Int): String = {
    def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
      val sb = new StringBuilder
      for (i <- 1 to length) {
        val randomNum = util.Random.nextInt(chars.length)
        sb.append(chars(randomNum))
      }
      sb.toString
    }

    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    randomStringFromCharList(length, chars)
  }

  object javascript {

    def display(flag: Boolean): String =
      if (flag) "block" else "none"

    @js.native
    @JSGlobal("displayLoadingAnimation")
    def displayLoadingAnimation(): Unit = js.native

  }

}
