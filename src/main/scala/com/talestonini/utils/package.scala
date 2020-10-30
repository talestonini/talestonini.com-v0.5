package com.talestonini

import java.time._
import java.time.format.DateTimeFormatter.{ofPattern => pattern}

package object utils {

  private val SimpleDateFormatter = pattern("dd/MM/yyyy")

  def datetime2Str(datetime: Option[ZonedDateTime]): String =
    datetime match {
      case Some(dt) => dt.format(SimpleDateFormatter)
      case None     => "no date"
    }

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

  object js {

    def display(flag: Boolean): String =
      if (flag) "block" else "none"

  }

}
