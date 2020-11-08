package com.talestonini

import java.time._
import java.time.format.DateTimeFormatter.{ofPattern => pattern}

package object utils {

  private val SimpleDateFormatter = pattern("dd/MM/yyyy")
  private val SimpleDateRegex     = "(\\d{2})/(\\d{2})/(\\d{4})".r

  def datetimeToStr(datetime: Option[ZonedDateTime]): String =
    datetime match {
      case Some(dt) => dt.format(SimpleDateFormatter)
      case None     => "no date"
    }

  def strToDatetime(str: String): ZonedDateTime = {
    str match {
      case SimpleDateRegex(dd, mm, yyyy) =>
        Instant.parse(s"${yyyy}-${mm}-${dd}T00:00:00.00Z").atZone(ZoneId.of("UTC"))
      case _ =>
        throw new IllegalArgumentException("datetime string should be in format dd/MM/yyyy")
    }
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
