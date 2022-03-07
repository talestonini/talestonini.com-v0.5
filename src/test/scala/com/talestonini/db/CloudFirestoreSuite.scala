package com.talestonini.db

import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite

class CloudFirestoreSuite extends CatsEffectSuite {

  test("should get an auth token") {
    CloudFirestore.getAuthTokenF() flatMap { token => IO(assertEquals(token.substring(0, 5), "eyJh")) }
  }

}
