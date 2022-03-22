package com.talestonini.db.model

import io.circe.syntax._
import java.time.ZonedDateTime
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AsyncWordSpec

class packageSpec extends AsyncWordSpec with Matchers {

  "the database model" should {

    val post = Post(
      Some("a resource"),
      Some("a title"),
      Some(ZonedDateTime.parse("2022-01-01T00:00:00Z")),
      Some(ZonedDateTime.parse("2022-03-21T19:20:33Z")),
      Some(true)
    )

    val expectedPostJson = """{
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

    val postDocBody = DocBody("a name", post)

    val expectedPostDocBodyJson = s"""{
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

    "convert a post to a JSON" in {
      post.asJson.toString shouldEqual expectedPostJson
    }

    "convert a post to a document body" in {
      postDocBody.asJson.toString shouldEqual expectedPostDocBodyJson
    }

  }

}
