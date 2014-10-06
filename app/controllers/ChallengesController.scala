package controllers

import play.api.mvc._
import java.util.UUID
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import mongo.ChallengeManager
import models.Challenge


/**
 * API for Challenges
 */
object ChallengesController extends Controller {
  def createChallenge() = Action.async { request =>
    request.body.asJson match {
      case Some(json) => json.asOpt[Challenge](Challenge.newChallengeReads) match {
        case Some(challenge) =>
          ChallengeManager.saveChallenge(challenge).map{ err =>
            if(err.ok)
              Ok(Json.toJson(challenge))
            else
              BadRequest
          }
        case None =>
          Future(BadRequest)
      }
      case None => Future(BadRequest)
    }

  }
  def getChallenge(id: String) = Action.async {
    try {
      val uuid = UUID.fromString(id)
      ChallengeManager.getChallenge(uuid).map {
        case Some(challenge) => Ok(Json.toJson(challenge))
        case None => NotFound
      }
    } catch {
      case e: Exception => Future(NotFound)
    }
  }

  def getChallenges() = Action.async {
    ChallengeManager.getChallenges.map{ challenges: List[Challenge] =>
      Ok(Json.toJson(challenges))
    }
  }

}
