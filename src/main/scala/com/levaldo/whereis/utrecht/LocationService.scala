package com.levaldo.whereis.utrecht

import akka.actor.ActorSystem
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService

import scala.util.{Failure, Success}

case class Address(address: String)
case class LatLon(lat: Double, lng: Double)
case class Location(location: LatLon)
case class LocationError(error: String)

object LocationJsonSupport extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val LatLonFormats = jsonFormat2(LatLon)
  implicit val LocationFormats = jsonFormat1(Location)
  implicit val AddressFormats = jsonFormat1(Address)
  implicit val ErrorFormats = jsonFormat1(LocationError)
}

class LocationService(implicit system: ActorSystem) extends HttpService {
  val key = "AIzaSyDhICqqXle2VtY66l7VQ1asAvw914CvFKg"

  def actorRefFactory = system
  import LocationJsonSupport._
  import MediaTypes._

  def run(): spray.routing.Route = {

    import system.dispatcher

    path("location") {
      post {
        respondWithMediaType(`application/json`) {
          entity(as[Address]) { address =>
            onComplete(GoogleRequests.getAddress(address, key)) {
              case Success(value) => complete(value.fold(identity(_), identity(_)))
              case Failure(ex) => complete(LocationError(ex.getMessage))
            }
          }
        }
      }
    }
  }
}

object LocationService {
  def apply()(implicit system: ActorSystem) = new LocationService().run()
}
