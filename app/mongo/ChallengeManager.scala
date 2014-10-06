package mongo

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.indexes.{IndexType, Index}
import java.util.{Date, UUID}
import models.Challenge
import scala.Some
import reactivemongo.api.indexes.Index
import models.Rule
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import scala.concurrent.Future

/**
 * Created by kevinli on 10/5/14.
 */
object ChallengeManager {
  val db = ReactiveMongoPlugin.db
  val collection: JSONCollection = db.collection[JSONCollection]("challenges")

  def init() {
    collection.indexesManager.ensure(new Index(Seq(("id", IndexType.Ascending)), Some("id"), true, true))
    collection.indexesManager.ensure(new Index(Seq(("members.id", IndexType.Ascending)), Some("memberId"), false, true))
  }

  def getChallenge(id: UUID): Future[Option[Challenge]] = {
    collection.find(Json.obj("id" -> id)).one[Challenge]
  }

  def getChallenges(): Future[List[Challenge]] = {
    collection.find(Json.obj()).cursor[Challenge].collect[List]()
  }

  def getChallengesForUser(id: UUID): Future[List[Challenge]] = {
    collection.find(Json.obj("members.id" -> id)).cursor[Challenge].collect[List]()
  }

  def saveChallenge(challenge: Challenge) = {
    collection.save(challenge)
  }

}
