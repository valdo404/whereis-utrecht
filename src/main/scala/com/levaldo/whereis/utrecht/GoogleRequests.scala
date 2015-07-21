package com.levaldo.whereis.utrecht

import java.net.URLEncoder

import akka.actor.ActorSystem
import argonaut.{Json, Parse}
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}

import scala.concurrent.Future


object GoogleRequests {
  type Expected = Either[LocationError, Location]

  def url(address: Address, key: String): String = {
    def encodeAddress(address: String): String = {
      URLEncoder.encode(address, "UTF-8")
    }

    s"https://maps.googleapis.com/maps/api/geocode/json?address=${encodeAddress(address.address)}&key=$key"
  }

  def extractLocation(js: Option[Json]): Option[Location] = {
    js.map((js: Json) => Location(LatLon(js.field("lat").flatMap(_.number).get, js.field("lng").flatMap(_.number).get)))
  }

  def extract(js: Json): Option[Location] = {
    extractLocation(js.field("results").
      flatMap(js => js.array).
      flatMap(arr => arr.headOption).
      flatMap(js => js.field("geometry")).
      flatMap(js => js.field("location")))
  }

  def parse(js: String): Expected = Parse.parse(js).fold(
    msg => Left(LocationError(msg)),
    js => if(extract(js).isDefined) Right(extract(js).get) else Left(LocationError("Incomplete response")))

  def getAddress(address: Address, key: String)(implicit system: ActorSystem): Future[Expected] = {
    import system.dispatcher
    def extraction: HttpResponse ⇒ Expected = response ⇒ parse(response.entity.data.asString)
    val pipeline: HttpRequest => Future[Expected] = sendReceive ~> extraction

    pipeline(Get(url(address, key)))
  }

}
