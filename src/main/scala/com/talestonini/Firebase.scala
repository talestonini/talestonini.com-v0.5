package com.talestonini

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.response.SimpleHttpResponse
import fr.hmil.roshttp.Protocol.HTTPS
import fr.hmil.roshttp.Method.POST
import fr.hmil.roshttp.body.PlainTextBody
import monix.execution.Scheduler.Implicits.{global => scheduler}
import io.circe._, io.circe.parser._


object Firebase {

  val ApiKey = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY"
  val ProjectId = "ttdotcom"
  val Database = "(default)"
  val FirestoreApi = "firestore.googleapis.com/v1"

  val commonHeaders = {
    "Access-Control-Allow-Origin" -> "*"
    "Access-Control-Allow-Headers" -> "Content-Type"
    "Access-Control-Allow-Methods" -> "POST"
    "Content-Type" -> "application/json"
  }

}


class Firebase {

  var token: String = ""

  def getAuthToken(): Future[String] = Future {
    HttpRequest()
      .withMethod(POST)
      .withProtocol(HTTPS)
      .withHost("identitytoolkit.googleapis.com")
      .withPath("/v1/accounts:signUp")
      .withQueryParameter("key", Firebase.ApiKey)
      .withHeaders(Firebase.commonHeaders)
      .send()
      .onComplete({
        case rawJson: Success[SimpleHttpResponse] => 
          val res: Json = parse(rawJson.get.body).getOrElse(Json.Null)
          token = (res \\ "idToken")(0).toString
        case e: Failure[SimpleHttpResponse] => 
          token = ""
      })
  }

}

