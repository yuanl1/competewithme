package models

import java.util.{Date, UUID}
import play.api.libs.json._

// JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax



case class ChallengeMember(
  id: UUID,
  checkins: List[Checkin] = List.empty[Checkin])


case class Challenge(
  id : UUID,
  name : String,
  startDate : Date,
  endDate : Date,
  reward: String,
  pendingMembers: List[UUID],
  members: List[UUID],
  visibility: Visibility,
  rule: Rule) {

  def hasMember(id: UUID): Boolean = {
    members.exists(_ == id)
  }

  def inviteMember(id: UUID): Option[Challenge] = {
    if(pendingMembers.exists(_ == id) || members.exists(_ == id)){
      None
    } else {
      Some(this.copy(pendingMembers = id :: pendingMembers))
    }
  }

  def joinChallenge(id: UUID): Option[Challenge] = {
    pendingMembers.find(_ == id).map{ x =>
      this.copy(
        pendingMembers = pendingMembers.filter(_ != id),
        members = x :: members
      )
    }
  }
}

object Challenge {
  val newChallengeReads: Reads[Challenge] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "startDate").read[Date] and
      (JsPath \ "reward").read[String] and
      (JsPath \ "visibility").read[Visibility] and
      (JsPath \ "rule").read[Rule]
  )(createNewChallenge _)

  private def createNewChallenge(name: String, startDate: Date, reward: String, visibility: Visibility, rule: Rule) : Challenge = {
    Challenge(UUID.randomUUID(),
      name,
      startDate,
      rule.getEndDate(startDate),
      reward,
      List.empty[UUID],
      List.empty[UUID],
      visibility,
      rule)
  }

  implicit val challengeFormatter = Json.format[Challenge]

  def withCheckins(checkins: List[Checkin]) = JsPath.json.update(
    JsPath.read[JsObject].map{ o => o ++ Json.obj("checkins" -> Json.toJson(checkins))}
  )

  def withUsers(users: List[User]) = JsPath.json.update(
    JsPath.read[JsObject].map{ o => o ++ Json.obj("members" -> Json.toJson(users))}
  )

  def withPendingUsers(users: List[User]) = JsPath.json.update(
    JsPath.read[JsObject].map{ o => o ++ Json.obj("pendingMembers" -> Json.toJson(users))}
  )

  def withAll(checkins: List[Checkin], users: List[User], pendingUsers: List[User]) = (
      withCheckins(checkins) and
      withUsers(users) and
      withPendingUsers(pendingUsers)
    ).reduce
}

