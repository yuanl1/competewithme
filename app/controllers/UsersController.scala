package controllers

import play.api.mvc._
import mongo.{ChallengeManager, UserManager}
import java.util.UUID
import models.{SessionToken, Checkin, User}
import play.api.libs.json.{JsSuccess, JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import util.{PasswordHelper, ControllerHelpers}


/**
 * API for Users
 */
object UsersController extends ControllerHelpers{

  implicit val userWrites = User.userWrites

  def createUser() = Action.async(parse.json){ request =>
    request.body.asOpt[User](User.newUserReads) match {
      case Some(newUser) =>
        UserManager.getUserByEmail(newUser.email).flatMap {
          case Some(user) =>
            Future(Conflict)
          case None =>
            UserManager.createUser(newUser).map{ err =>
              if(err.ok) {
                Ok(Json.toJson(newUser.session.get))
              } else {
                BadRequest
              }
            }
        }
      case None =>
        Future(BadRequest)
    }
  }

  def login() = Action.async(parse.json) { request =>
    ((request.body \ "email").validate[String], (request.body \ "password").validate[String]) match {
      case (JsSuccess(email, _), JsSuccess(password, _)) =>
        UserManager.getUserByEmail(email).flatMap {
          case Some(user) if PasswordHelper.matchPassword(password, user.password) =>
            val session = SessionToken.create()
            UserManager.updateUser(user.updateSession(session)).map{ err =>
              if(err.ok){
                Ok(Json.toJson(session))
              } else {
                InternalServerError("Failed to update user")
              }
            }

          case _ => Future.successful(BadRequest("Unable to login"))
        }
      case _ => Future.successful(BadRequest("Must provide an email and a password."))
    }
  }

  def getUser = Authenticated { case (request , user) =>
    Future.successful(Ok(Json.toJson(user)))
  }

  def getChallengesForUser = Authenticated { case (request, user) =>
    ChallengeManager.getChallengesForUser(user).map{ challenges =>
      Ok(Json.toJson(challenges))
    }
  }

  def getInvitesForUser = Authenticated { case (request, user) =>
    ChallengeManager.getChallengeInvitesForUser(user).map{ challenges =>
      Ok(Json.toJson(challenges))
    }
  }

}
