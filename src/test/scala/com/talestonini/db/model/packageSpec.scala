package com.talestonini.db.model

import io.circe.syntax._
import org.http4s.circe.jsonEncoderOf
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AsyncWordSpec
import java.time.ZonedDateTime

class packageSpec extends AsyncWordSpec with Matchers {

  val post = Post(
    Some("a resource"),
    Some("a title"),
    Some(ZonedDateTime.parse("2022-01-01T00:00:00Z")),
    Some(ZonedDateTime.parse("2022-03-21T19:20:33Z")),
    Some(true)
  )

  val postBody = Body("a name", post)

  "the database model" should {

    "convert a post to a JSON" in {
      post.asJson.toString shouldEqual
        """{
          |  "resource" : {
          |    "stringValue" : "a resource"
          |  },
          |  "title" : {
          |    "stringValue" : "a title"
          |  },
          |  "first_publish_date" : {
          |    "timestampValue" : "2022-01-01T00:00:00.000Z"
          |  },
          |  "publish_date" : {
          |    "timestampValue" : "2022-03-21T19:20:33.000Z"
          |  },
          |  "enabled" : {
          |    "booleanValue" : true
          |  }
          |}""".stripMargin
    }

    "convert a post to a body" in {
      postBody.asJson.toString shouldEqual
        """{
          |  "name" : "a name",
          |  "fields" : {
          |    "resource" : {
          |      "stringValue" : "a resource"
          |    },
          |    "title" : {
          |      "stringValue" : "a title"
          |    },
          |    "first_publish_date" : {
          |      "timestampValue" : "2022-01-01T00:00:00.000Z"
          |    },
          |    "publish_date" : {
          |      "timestampValue" : "2022-03-21T19:20:33.000Z"
          |    },
          |    "enabled" : {
          |      "booleanValue" : true
          |    }
          |  }
          |}""".stripMargin
    }

  }

}
