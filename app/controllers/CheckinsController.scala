package controllers

import play.api.mvc._
import java.util.UUID
import play.api.libs.json.{JsArray, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.Checkin
import mongo.{ChallengeManager, UserManager, CheckinManager}
import util.ControllerHelpers

/**
 * Created by kevinli on 10/13/14.
 */
object CheckinsController extends ControllerHelpers {

  def createCheckin() = AuthenticatedJson { case (request, user) =>
    request.body.asOpt[Checkin](Checkin.newCheckinReads(user)) match {
      case Some(checkin) =>
        val toFlatten = for{
          challengeOpt <- ChallengeManager.getChallenge(checkin.challenge)
          checkins <- CheckinManager.get(checkin.user, checkin.challenge)
        } yield {
          if(challengeOpt.isDefined) {
            val challenge = challengeOpt.get
            if(challenge.hasMember(user) && challenge.rule.canCheckin(checkins)){
              CheckinManager.create(checkin).map { error =>
                if (error.ok) {
                  Created(Json.toJson(checkin))
                } else {
                  InternalServerError(Json.obj("error" -> error.message))
                }
              }
            } else {
              Future(BadRequest(Json.obj("error" -> "Checkin requirements not met")))
            }
          } else {
            Future(BadRequest(Json.obj("error" -> "Checkin requirements not met")))
          }
        }
        toFlatten.flatMap(x => x)
      case None => Future(BadRequest(Json.obj("error" -> "Must provide challenge id and message")))
    }

  }

}
