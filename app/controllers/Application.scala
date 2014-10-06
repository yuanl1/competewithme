package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.Json
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import mongo.ChallengeManager

object Application extends Controller with MongoController{
  def collection: JSONCollection = db.collection[JSONCollection]("users")

  def index = Action {
    Ok("ok")
  }

}