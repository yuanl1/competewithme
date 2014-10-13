package controllers

import play.api.mvc._
import java.util.UUID
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.Checkin
import mongo.{ChallengeManager, UserManager, CheckinManager}

/**
 * Created by kevinli on 10/13/14.
 */
object CheckinsController extends Controller {

  def createCheckin() = Action.async(parse.json) { request =>
    request.body.asOpt[Checkin](Checkin.newCheckinReads) match {
      case Some(checkin) =>
        val toFlatten = for{
          userOpt <- UserManager.getUser(checkin.user)
          challengeOpt <- ChallengeManager.getChallenge(checkin.challenge)
          checkins <- CheckinManager.get(checkin.user, checkin.challenge)
        } yield {
          if(userOpt.isDefined && challengeOpt.isDefined) {
            val challenge = challengeOpt.get
            if(challenge.hasMember(checkin.user) && challenge.rule.canCheckin(checkins)){
              CheckinManager.create(checkin).map { error =>
                if (error.ok) {
                  Created(Json.toJson(checkin))
                } else {
                  BadRequest(Json.obj("error" -> error.message))
                }
              }
            } else {
              Future(BadRequest(Json.obj("error" -> "Checkin requirements not met")))
            }
          } else {
            Future(BadRequest)
          }
        }
        toFlatten.flatMap(x => x)
      case None => Future(BadRequest)
    }

  }

}
