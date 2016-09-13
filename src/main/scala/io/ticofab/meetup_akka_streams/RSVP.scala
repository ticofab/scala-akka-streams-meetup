package io.ticofab.meetup_akka_streams

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class RSVP(venue: Option[Venue],
                visibility: String,
                response: String,
                guests: Int,
                member: Option[Member])

case class Venue(venue_name: String,
                 lon: Long,
                 lat: Long,
                 venue_id: Int)

case class Member(member_id: Int,
                  member_name: String)

trait RSVPJsonFormatSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val memberFormat = jsonFormat2(Member)
  implicit val venueFormat = jsonFormat4(Venue)
  implicit val RSVPFormat = jsonFormat5(RSVP)
}