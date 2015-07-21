package com.levaldo.whereis.utrecht

import argonaut.Parse
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest

class WhereIsUtrechtSpec extends Specification with Specs2RouteTest with HttpService with NoTimeConversions {
  def actorRefFactory = system // connect the DSL to the test ActorSystem

  val smallRoute = LocationService()
  import scala.concurrent.duration._

  implicit val routeTestTimeout = RouteTestTimeout(5.seconds)

  import spray.http.HttpCharsets._
  import spray.http.MediaTypes._
  import spray.http.StatusCodes._
  import spray.http.{ContentType, HttpEntity}
  "The service" should {

    "return a bad request when called without post content" in {
      Post("/location") ~> sealRoute(smallRoute) ~> check {
        status === BadRequest

        responseAs[String] must contain("Request entity expected but not supplied")
      }
    }

    "return a valid response" in {
      val body = HttpEntity(
        contentType = ContentType(`application/json`, `UTF-8`),
        string = """{"address": "Eendrachtlaan 315, Utrecht"}"""
      )

      Post("/location", body) ~> sealRoute(smallRoute) ~> check {
        mediaType === `application/json`

        status === OK
        Parse.parseOption(responseAs[String]).get.nospaces === """{"location":{"lat":52.0618174,"lng":5.1085974}}"""
      }
    }


    "return a MethodNotAllowed error for GET requests to the location path" in {
      Get("/location") ~> sealRoute(smallRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: POST"
      }
    }
  }
}