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

}
