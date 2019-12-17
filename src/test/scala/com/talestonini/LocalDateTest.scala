package com.talestonini

import org.scalatest._
import Matchers._

import java.time._
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern


class LocalDateTest extends FunSpec {

  describe("Parsing dates") {
    it("should parse a date from a datetime string") {
      val str = "2019-11-10T09:55:34.276Z"
      val datetime = LocalDateTime.parse(str, ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
      datetime.format(ofPattern("dd/MM/yyyy")) shouldBe "10/11/2019"
    }
  }

}

