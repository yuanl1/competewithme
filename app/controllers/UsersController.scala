package controllers

import play.api.mvc._
import mongo.{CheckinManager, ChallengeManager, UserManager}
import java.util.UUID
import models.{Checkin, User}
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger


/**
 * API for Users
 */
object UsersController extends Controller {

  def getUsers() = Action.async {
    UserManager.getUsers.map{ users =>
      val result = users.foldLeft(JsArray()){ (arr, user) =>
        arr.append(Json.toJson(user).transform(User.withoutPassword).get)
      }
      Ok(result)
    }
  }

  def getUser(id: UUID) = Action.async {
    UserManager.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user).transform(User.withoutPassword).get)
      case None => NotFound
    }
  }

  def getChallengesForUser(id: UUID) = Action.async {
    ChallengeManager.getChallengesForUser(id).map{ challenges =>
      Ok(Json.toJson(challenges))
    }
  }

  def getInvitesForUser(id: UUID) = Action.async {
    ChallengeManager.getPendingChallengesForUser(id).map { challenges =>
      Ok(Json.toJson(challenges))
    }
  }

  def getCheckinsForUser(userId: UUID) = Action.async {
    CheckinManager.getByUser(userId).map { checkins =>
      Ok(Json.toJson(checkins))
    }
  }

  def joinChallenge(userId: UUID, challengeId: UUID) = Action.async {
    ChallengeManager.getChallenge(challengeId).flatMap {
      case Some(challenge) =>
        val newChallengeOpt = challenge.joinChallenge(userId)
        if(newChallengeOpt.isDefined) {
          ChallengeManager.updateChallenge(newChallengeOpt.get).map { error =>
            if(error.ok) {
              Ok(Json.toJson(newChallengeOpt.get))
            } else {
              BadRequest(Json.obj("error" -> error.message))
            }
          }
        } else {
          Future(Ok(Json.toJson(challenge)))
        }
      case None => Future(NotFound)
    }
  }

  def createUser() = Action.async(parse.json){ request =>
    request.body.asOpt[User](User.newUserReads) match {
      case Some(newUser) =>
        UserManager.createUser(newUser).map{ err =>
          if(err.ok) {
            Created(Json.toJson(newUser))
          } else {
            BadRequest
          }
        }
      case None =>
        Future(BadRequest)
    }
  }
}
