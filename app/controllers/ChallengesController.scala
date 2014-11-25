package controllers

import play.api.mvc._
import java.util.{Date, UUID}
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import mongo.{UserManager, ChallengeManager}
import models._
import reactivemongo.core.commands.LastError
import scala.Some
import util.ControllerHelpers


/**
 * API for Challenges
 */
object ChallengesController extends ControllerHelpers {
  implicit val userWrites = User.userWrites

  def getChallenge(id: UUID) = Authenticated { case (request, user) =>
    ChallengeManager.getChallenge(id).map {
      case Some(challenge) if challenge.hasMember(user) => Ok(Json.toJson(challenge))
      case None => Unauthorized
    }
  }

  def getChallengeMembers(id: UUID) = Authenticated { case (request, user) =>
    ChallengeManager.getChallenge(id).flatMap{
      case Some(challenge) if challenge.hasMember(user) =>
        UserManager.getUsersInChallenge(challenge).map { users =>
          Ok(Json.toJson(users))
        }
      case None => Future(Unauthorized)
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
              InternalServerError(Json.obj("error" -> error.message))
            }
          }
        } else {
          Future(BadRequest(Json.obj("error" -> "Unable to join challenge")))
        }
      case None => Future(NotFound)
    }
  }

  def createCheckin(id: UUID) = AuthenticatedJson { case (request, user) =>
    request.body.asOpt[Checkin](Checkin.newCheckinReads) match {
      case Some(checkin) =>
        ChallengeManager.getChallenge(id).flatMap {
          case Some(challenge) if challenge.canCheckin(user) =>
            val newChallenge = challenge.checkin(user, checkin)
            ChallengeManager.updateChallenge(newChallenge).map { error =>
              if(error.ok) {
                Ok(Json.toJson(newChallenge))
              } else {
                InternalServerError(Json.obj("error" -> error.message))
              }
            }
          case Some(challenge) => Future(BadRequest(Json.obj("error" -> "Checkin requirements not met")))
          case None => Future(BadRequest(Json.obj("error" -> "Challenge Not Found")))
        }
      case None => Future(BadRequest(Json.obj("error" -> "Must provide challenge message")))
    }
  }


  def createChallenge() = AuthenticatedJson { case (request, user) =>
    request.body.asOpt[Challenge](Challenge.newChallengeReads(user)) match {
      case Some(challenge) =>
        ChallengeManager.createChallenge(challenge).map{ error =>
          if (error.ok) {
            Ok(Json.toJson(challenge))
          } else {
            InternalServerError(Json.obj("error" -> error.message))
          }
        }
      case None =>
        Future(BadRequest)
    }
  }

}
