package controllers

import play.api.mvc._
import mongo.{ChallengeManager, UserManager}
import java.util.UUID
import models.DBUser
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger


/**
 * API for Users
 */
object UsersController extends Controller {

  def getUsers() = Action.async {
    UserManager.getUsers.map{ users =>
      Ok(Json.toJson(users))
    }
  }

  def getUser(id: String) = Action.async {
    try{
      val uuid = UUID.fromString(id)
      UserManager.getUser(uuid).map { userOpt =>
        userOpt match {
          case Some(user) => Ok(Json.toJson(user))
          case None => NotFound
        }
      }
    } catch {
      case e: Exception => Future(NotFound)
    }
  }

  def createUser() = Action.async{ request =>
    request.body.asJson match {
      case Some(json) => json.asOpt[DBUser](DBUser.newUserReads) match {
        case Some(newUser) =>
          UserManager.saveUser(newUser).map{ err =>
            if(err.ok) {
              Ok(Json.toJson(newUser))
            } else {
              BadRequest
            }
          }
        case None =>
          Future(BadRequest)
      }
      case None =>
        Future(BadRequest)
    }
  }


  def getChallengesForUser(id: String) = Action.async {
    try{
      val uuid = UUID.fromString(id)
      ChallengeManager.getChallengesForUser(uuid).map { challenges =>
        Ok(Json.toJson(challenges))
      }
    } catch {
      case e: Exception => Future(NotFound)
    }

  }

}
