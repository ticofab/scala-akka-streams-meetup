package io.ticofab.meetup_akka_streams

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing}
import akka.util.ByteString
import spray.json.JsonParser

import scala.concurrent.ExecutionContext.Implicits.global

object ApplicationMain extends App with RSVPJsonFormatSupport {

  implicit val system = ActorSystem("MyActorSystem")
  implicit val materializer = ActorMaterializer()

  val httpRequest = HttpRequest(
    method = HttpMethods.GET,
    uri = "http://stream.meetup.com/2/rsvps"
  )

  Http().singleRequest(httpRequest).map { response =>
    println(response.status)
    response.status match {
      case StatusCodes.OK =>

        // delimiting frame to make sure we take items when separated by a line-break
        val frame = Flow[ByteString]
          .via(Framing.delimiter(
            delimiter = ByteString("\n"),
            maximumFrameLength = 3000,
            allowTruncation = false))

        // get the response entity as a source and manipulate it
        response.entity.dataBytes
          .via(frame)
          .map(_.utf8String)
          .map(JsonParser(_).convertTo[RSVP])
          .runForeach(println)

      case _ =>
        println("Unexpected error, shutting down")
        system.terminate();
    }
  }

}

