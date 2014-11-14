package controllers

import play.api.mvc._
import mongo.{CheckinManager, ChallengeManager, UserManager}
import java.util.UUID
import models.{SessionToken, Checkin, User}
import play.api.libs.json.{JsSuccess, JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import util.ControllerHelpers


/**
 * API for Users
 */
object UsersController extends ControllerHelpers{

  implicit val userWrites = User.userWrites

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

  def login() = Action.async(parse.json) { request =>
    ((request.body \ "email").validate[String], (request.body \ "password").validate[String]) match {
      case (JsSuccess(email, _), JsSuccess(password, _)) =>
        UserManager.getUserByEmail(email).flatMap{
          case Some(user) =>
            val session = SessionToken.create()
            UserManager.updateUser(user.updateSession(session)).map{ err =>
              if(err.ok){
                Ok(Json.toJson(session))
              } else {
                InternalServerError("Failed to update user")
              }
            }

          case None => Future.successful(BadRequest("User not found"))
        }
      case _ => Future.successful(BadRequest("Must provide an email and a password."))
    }
  }

  def getUser() = Authenticated { user =>
    Future.successful(Ok(Json.toJson(user)))
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

}
