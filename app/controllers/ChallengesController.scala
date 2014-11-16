package controllers

import play.api.mvc._
import java.util.{Date, UUID}
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import mongo.{UserManager, CheckinManager, ChallengeManager}
import models._
import reactivemongo.core.commands.LastError
import scala.Some
import util.ControllerHelpers


/**
 * API for Challenges
 */
object ChallengesController extends ControllerHelpers {
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
    } yield {
      challengeOpt match {
        case Some(challenge) =>
          Ok(Json.toJson(challenge))
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

  def inviteUserToChallenge(challengeId: UUID, userId: UUID) = Authenticated { case (request, user) =>
    val toFlatten = for {
      challengeOpt <- ChallengeManager.getChallenge(challengeId)
      userOpt <- UserManager.getUser(userId)
    } yield {
      (userOpt, challengeOpt) match {
        case (Some(userToInvite), Some(challenge)) if challenge.hasOwner(user) =>
          challenge.inviteMember(userToInvite) match {
            case Some(newChallenge) =>
              ChallengeManager.updateChallenge(newChallenge).map{ error =>
                if(error.ok) {
                  Ok(Json.toJson(newChallenge))
                } else {
                  InternalServerError(Json.obj("error" -> error.message))
                }
              }
            case None => Future(BadRequest(Json.obj("error" -> "User cannot be invited")))
          }
        case _ => Future(BadRequest)
      }
    }
    toFlatten.flatMap(x => x)
  }

  def joinChallenge(id: UUID) = Authenticated { case (request, user) =>
    ChallengeManager.getChallenge(id).flatMap {
      case Some(challenge) =>
        val newChallengeOpt = challenge.joinChallenge(user)
        if(newChallengeOpt.isDefined) {
          ChallengeManager.updateChallenge(newChallengeOpt.get).map { error =>
            if(error.ok) {
              Ok(Json.toJson(newChallengeOpt.get))
            } else {
              BadRequest(Json.obj("error" -> error.message))
            }
          }
        } else {
          Future(BadRequest(Json.obj("error" -> "Unable to join challenge")))
        }
      case None => Future(NotFound)
    }
  }

  def createChallenge() = AuthenticatedJson { case (request, user) =>
    request.body.asOpt[Challenge](Challenge.newChallengeReads(user)) match {
      case Some(challenge) =>
        ChallengeManager.createChallenge(challenge).map{ err =>
          if (err.ok)
            Ok(Json.toJson(challenge))
          else
            BadRequest
        }
      case None =>
        Future(BadRequest)
    }
  }

}
