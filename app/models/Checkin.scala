package models

import java.util.{Date, UUID}
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by kevinli on 9/23/14.
 */
case class Checkin(
  id: UUID,
  user: UUID,
  challenge: UUID,
  message: String,
  date: Date){
}

object Checkin {
  implicit val checkinFormat = Json.format[Checkin]
}
