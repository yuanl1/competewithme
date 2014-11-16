package models

import java.util.{Date, UUID}
import play.api.libs.json._

// JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax



case class ChallengeMember(
  id: UUID,
  name: String,
  joined: Boolean,
  role: Role)

object ChallengeMember {
  implicit val challengeMemberFormatter = Json.format[ChallengeMember]
}


case class Challenge(
  id : UUID,
  name : String,
  startDate : Date,
  endDate : Date,
  reward: String,
  members: Set[ChallengeMember],
  visibility: Visibility,
  rule: Rule) {

  def hasOwner(user: User): Boolean = {
    members.exists(member => member.id == user.id && member.role == Role.Owner)
  }
  def hasMember(user: User): Boolean = {
    members.exists(_.id == user.id)
  }

  def inviteMember(user: User): Option[Challenge] = {
    if(hasMember(user)) {
      None
    } else {
      Some(this.copy(members = members + ChallengeMember(user.id, user.name, false, Role.Member)))
    }
  }

  def joinChallenge(user: User): Option[Challenge] = {
    members.find(_.id == user.id).flatMap{ challengeMember =>
      if(!challengeMember.joined){
        Some(this.copy(members = members - challengeMember + ChallengeMember(user.id, user.name, true, Role.Member)))
      } else {
        None
      }
    }
  }
}

object Challenge {
  def newChallengeReads(user: User): Reads[Challenge] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "startDate").read[Date] and
      (JsPath \ "reward").read[String] and
      (JsPath \ "visibility").read[Visibility] and
      (JsPath \ "rule").read[Rule]
  )(createNewChallenge(user) _)

  private def createNewChallenge(user: User)(name: String, startDate: Date, reward: String, visibility: Visibility, rule: Rule) : Challenge = {
    Challenge(UUID.randomUUID(),
      name,
      startDate,
      rule.getEndDate(startDate),
      reward,
      Set(ChallengeMember(user.id, user.name, true, Role.Owner)),
      visibility,
      rule)
  }

  implicit val challengeFormatter = Json.format[Challenge]

  def withCheckins(checkins: List[Checkin]) = JsPath.json.update(
    JsPath.read[JsObject].map{ o => o ++ Json.obj("checkins" -> Json.toJson(checkins))}
  )

  def withUsers(users: List[User]) = {
    implicit val userWrite = User.userWrites
    JsPath.json.update(
      JsPath.read[JsObject].map{ o => o ++ Json.obj("members" -> Json.toJson(users))}
    )
  }

  def withPendingUsers(users: List[User]) = {
    implicit val userWrite = User.userWrites
    JsPath.json.update(
      JsPath.read[JsObject].map{ o => o ++ Json.obj("pendingMembers" -> Json.toJson(users))}
    )
  }

  def withAll(checkins: List[Checkin], users: List[User], pendingUsers: List[User]) = (
      withCheckins(checkins) and
      withUsers(users) and
      withPendingUsers(pendingUsers)
    ).reduce
}

