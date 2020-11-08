package com.talestonini.db

import com.talestonini.BaseSpec
import com.talestonini.db.CloudFirestore
import com.talestonini.db.model._
import com.talestonini.utils._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

class CloudFirestoreSpec extends BaseSpec {

  val token =
    "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQxOTI5ZmY0NWM2MDllYzRjNDhlYmVmMGZiMTM5MmMzOTEzMmQ5YTEiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiVGFsZXMgVG9uaW5pIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdoclBEYmw0cnVnMXFuNzF3YXBISzJ0NHFYTVV0UkZTZVJ3aDdYZnhBIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3R0ZG90Y29tIiwiYXVkIjoidHRkb3Rjb20iLCJhdXRoX3RpbWUiOjE2MDQ4MzE4MzMsInVzZXJfaWQiOiJ3Q2NzWVQ5c2VIYjZaVFBNT2Z5dlRDNFR0YWMyIiwic3ViIjoid0Njc1lUOXNlSGI2WlRQTU9meXZUQzRUdGFjMiIsImlhdCI6MTYwNDgzMTgzMywiZXhwIjoxNjA0ODM1NDMzLCJlbWFpbCI6InRhbGVzdG9uaW5pQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTE2NzU2MzkwMzIxMDEwMzc5MTU5Il0sImdpdGh1Yi5jb20iOlsiNjYyMjQzMiJdLCJlbWFpbCI6WyJ0YWxlc3RvbmluaUBnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJnaXRodWIuY29tIn19.p3DN_Z1LEbQVqRF-w04LbEcFcMx3HbzubnGrqwZAd4YPV3YYJil5oexwR9iJ2--qaWmkqIElRajTgorwDZIdNFeFH6Gqz_wZc9eHWK7UilRZ5KC2kArhPWkJR6ZdPm6rpM7eJoTw6o2OwrhP3TG7bn2hRmKqHWRDwQbusjfaM5Hw-y7T4ANUFsFsuwBP78rETcYrqe2ZCm_CtRYvWkeEut-bOitVioCmrrHSPxojboWZmgmj_BUSDz-KU0TW_HRHAm3xHFsxf09GHjTrJ7Oz-Is_5-3JacCE-qPbg_0mjc_KBi1F9xszP9U68SyZSo0rdlfFsrvhE74m1DM3bIzlVg"

  "the Cloud Firestore interface" should {

    "be able to upsert posts" in {
      val post = Post(
        title = Some("Eliiza Urban Forest Challenge"),
        resource = Some("urbanForestChallenge"),
        firstPublishDate = Some(strToDatetime("25/09/2018")),
        publishDate = Some(strToDatetime("25/09/2018"))
      )

      CloudFirestore
        .createPost(token, post)
        .onComplete({
          case doc: Success[Doc[Post]] =>
            println("success creating post")
          case f: Failure[Doc[Post]] =>
            println(s"failed creating post: ${f.exception}")
        })
    }

  }

}
