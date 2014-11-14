package controllers

import play.api.mvc._
import java.util.UUID
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import mongo.{UserManager, CheckinManager, ChallengeManager}
import models.{User, Challenge}
import reactivemongo.core.commands.LastError


/**
 * API for Challenges
 */
object ChallengesController extends Controller {
  implicit val userWrites = User.userWrites


  def getChallenges() = Action.async {
    ChallengeManager.getChallenges.map{ challenges =>
      Ok(Json.toJson(challenges))
    }
  }

  def getChallenge(id: UUID) = Action.async {
    ChallengeManager.getChallenge(id).map { challenge =>
      Ok(Json.toJson(challenge))
    }
  }

  def getChallengeAll(id: UUID) = Action.async {
    for {
      challengeOpt <- ChallengeManager.getChallenge(id)
      checkins <- CheckinManager.getByChallenge(id)
      users <- if (challengeOpt.isDefined) UserManager.getUsersInChallenge(challengeOpt.get) else Future(List.empty[User])
      pendingUsers <- if (challengeOpt.isDefined) UserManager.getPendingUsersInChallenge(challengeOpt.get) else Future(List.empty[User])
    } yield {
      challengeOpt match {
        case Some(challenge) =>
          Ok(Json.toJson(challenge).transform(Challenge.withAll(checkins, users, pendingUsers)).get)
        case None => NotFound
      }
    }
  }

  def getChallengeCheckins(challengeId: UUID) = Action.async {
    CheckinManager.getByChallenge(challengeId).map { checkins =>
      Ok(Json.toJson(checkins))
    }
  }

  def getChallengeMembers(id: UUID) = Action.async {
    ChallengeManager.getChallenge(id).flatMap{
      case Some(challenge) =>
        UserManager.getUsersInChallenge(challenge).map { users =>
          Ok(Json.toJson(users))
        }
      case None => Future(NotFound)
    }
  }

  def inviteUserToChallenge(challengeId: UUID, userId: UUID) = Action.async {
    val toFlatten = for {
      userOpt <- UserManager.getUser(userId)
      challengeOpt <- ChallengeManager.getChallenge(challengeId)
    } yield {
      (userOpt, challengeOpt) match {
        case (Some(user), Some(challenge)) =>
          challenge.inviteMember(user.id) match {
            case Some(newChallenge) =>
              ChallengeManager.updateChallenge(newChallenge).map{ error =>
                if(error.ok) {
                  Ok(Json.toJson(newChallenge))
                } else {
                  BadRequest(Json.obj("error" -> error.message))
                }
              }
            case None => Future(Ok(Json.toJson(challenge)))
          }
        case _ => Future(BadRequest)
      }
    }
    toFlatten.flatMap(x => x)
  }

  def createChallenge() = Action.async(parse.json) { request =>
    request.body.asOpt[Challenge](Challenge.newChallengeReads) match {
      case Some(challenge) =>
        ChallengeManager.createChallenge(challenge).map{ err =>
          if(err.ok)
            Ok(Json.toJson(challenge))
          else
            BadRequest
        }
      case None =>
        Future(BadRequest)
    }
  }

}
