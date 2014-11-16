package models

import java.util.{Date, UUID}
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by kevinli on 9/23/14.
 */
case class Checkin(
  user: UUID,
  challenge: UUID,
  message: String,
  date: Date){
}

object Checkin {
  implicit val checkinFormat = Json.format[Checkin]
  def newCheckinReads(user: User): Reads[Checkin] = (
      (JsPath \ "challenge").read[UUID] and
      (JsPath \ "message").read[String]
    )(createNewCheckin(user.id) _)


  def createNewCheckin(userId: UUID)(challengeId: UUID, message: String): Checkin = {
    Checkin(user=userId, challenge=challengeId, message=message, date= new Date)
  }
}