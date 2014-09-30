package models

import java.util.{Date, UUID}

/**
 * Created by kevinli on 9/23/14.
 */
case class Checkin(
  id: UUID,
  user: UUID,
  challenge: UUID,
  date: Date){
}

object Checkin {
  import play.api.libs.json.Json
  implicit val checkinFormat = Json.format[Checkin]
}
