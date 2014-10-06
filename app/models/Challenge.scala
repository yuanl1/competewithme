package models

import java.util.{Date, UUID}
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


case class Member(
  id: UUID,
  checkins: List[Checkin] = List.empty[Checkin])

object Member {
  implicit val memberFormatter = Json.format[Member]
}


case class Challenge(
  id : UUID,
  name : String,
  startDate : Date,
  endDate : Date,
  reward: String,
  members: List[Member],
  rule: Rule)

object Challenge {
  val newChallengeReads: Reads[Challenge] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "startDate").read[Date] and
      (JsPath \ "reward").read[String] and
      (JsPath \ "members").read[List[UUID]] and
      (JsPath \ "rule").read[Rule]
  )(createNewChallenge _)

  private def createNewChallenge(name: String, startDate: Date, reward: String, members: List[UUID], rule: Rule) : Challenge = {
    val membersList = members.map(Member(_))
    Challenge(UUID.randomUUID(), name, startDate, rule.getEndDate(startDate), reward, membersList, rule)
  }
  implicit val challengeFormatter = Json.format[Challenge]
}

