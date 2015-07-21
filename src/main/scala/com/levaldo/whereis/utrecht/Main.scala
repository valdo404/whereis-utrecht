package com.levaldo.whereis.utrecht

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

object Main extends App with SimpleRoutingApp {

  implicit val system = ActorSystem()

  startServer(interface = "localhost", port = 8080) {
    LocationService()
  }
}
