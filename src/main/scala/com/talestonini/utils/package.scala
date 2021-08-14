package com.talestonini

import java.time._
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.{ofPattern => pattern}

import com.thoughtworks.binding.Binding.Var
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

package object utils {

  // --- UI ------------------------------------------------------------------------------------------------------------

  private var lengthyOperationsInPlace: Set[String] = Set.empty

  def displayLoading(isLoading: Var[Boolean], forOperation: String): Unit = {
    lengthyOperationsInPlace = lengthyOperationsInPlace + forOperation
    isLoading.value = true
  }

  def hideLoading(isLoading: Var[Boolean], forOperation: String): Unit = {
    lengthyOperationsInPlace = lengthyOperationsInPlace - forOperation
    if (lengthyOperationsInPlace.isEmpty) isLoading.value = false
  }

  object javascript {

    def display(flag: Boolean): String =
      if (flag) "block" else "none"

  }

  // --- general -------------------------------------------------------------------------------------------------------

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

  @js.native
  @JSGlobal("sendGtagEvent")
  def sendGtagEvent(eventName: String, pagePath: String): Unit = js.native

  // --- datetime ------------------------------------------------------------------------------------------------------

  private val UTCZoneId = ZoneId.of("UTC")

  val InitDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), UTCZoneId)

  val SimpleDateFormatter      = pattern("dd LLL yyyy")
  val DateTimeCompareFormatter = pattern("yyyyMMddhhmmss")

  def datetime2Str(datetime: ZonedDateTime, fmt: DateTimeFormatter): String =
    datetime
      .toInstant()
      .atZone(ZoneId.systemDefault())
      .format(fmt)

  def datetime2Str(
    datetime: Option[ZonedDateTime],
    default: String = "no date",
    fmt: DateTimeFormatter = SimpleDateFormatter
  ): String =
    datetime match {
      case Some(dt) => datetime2Str(dt, fmt)
      case None     => default
    }

  def now(): ZonedDateTime = ZonedDateTime.now(UTCZoneId)

}
