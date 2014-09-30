package controllers

import play.api.mvc._
import mongo.UserManager
import java.util.UUID
import models.{NewUser, User}
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.core.commands.LastError
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
      UserManager.getUser(uuid).map { userOpt:Option[User] =>
        userOpt match {
          case Some(user: User) => Ok(Json.toJson(user))
          case None => NotFound
        }
      }
    } catch {
      case e: Exception => Future(NotFound)
    }
  }

  def createUser() = Action.async{ request =>
    request.body.asJson match {
      case Some(json) => json.asOpt[NewUser] match {
        case Some(newUser) =>
          UserManager.createUser(newUser).map{ case (err, user) =>
            if(err.ok) {
              Logger.info("User created: " + user.id.toString)
              Ok(Json.toJson(user))
            } else {
              Logger.error("User create failed: " + err.errMsg)
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

}
