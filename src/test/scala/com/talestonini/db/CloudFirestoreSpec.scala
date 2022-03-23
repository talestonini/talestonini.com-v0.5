package com.talestonini.db

import com.talestonini.db.model._
import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AsyncWordSpec
import java.time.ZonedDateTime
import scala.concurrent.Promise
import scala.util.Success
import scala.util.Failure

class CloudFirestoreSpec extends AsyncWordSpec with Matchers {

  // see http://doc.scalatest.org/3.0.0/index.html#org.scalatest.AsyncWordSpec
  // for Scala.js
  implicit override def executionContext = org.scalatest.concurrent.TestExecutionContext.runNow

  "the database object" should {

    "get an auth token" in {
      CloudFirestore.getAuthToken() map { token => token should startWith("eyJh") }
    }

    "get a post" in {
      // let's get the post about Scala Decorators
      CloudFirestore.getPosts() map { posts =>
        posts.filter(p => p.fields.title.get == "Decorators in Scala") should not be empty
      }
    }

    // "create a comment, get all comments and delete a comment" in {
    // val postPath    = "projects/ttdotcom/databases/(default)/documents/posts/nnpTGgZd4t5SBetL6lY9"
    // val heather     = User(Some("Heather Miller"), Some("heather.miller@cs.cmu.edu"), Some("111"))
    // val text        = "Great post!"
    // val niceComment = Comment(Some(heather), Some(ZonedDateTime.now()), Some(text))

    // for {
    // newComment  <- CloudFirestore.createComment(postPath, niceComment)
    // allComments <- CloudFirestore.getComments(postPath)
    // res         <- CloudFirestore.removeComment(newComment.name)
    // } yield {
    // newComment.fields.text.get should be(text)
    // allComments.map(cs => cs.name).contains(newComment.name) should be(true)
    // if (res.isEmpty) succeed else fail
    // }
    // }

  }

}
