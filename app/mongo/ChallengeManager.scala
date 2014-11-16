package mongo

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.indexes.{IndexType, Index}
import java.util.{Date, UUID}
import models.{User, Challenge, Rule}
import scala.Some
import reactivemongo.api.indexes.Index
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

  def getChallengesForUser(user: User): Future[List[Challenge]] = {
    val query = Json.obj("members" -> Json.obj("$elemMatch" -> Json.obj("id" -> user.id, "joined" -> true)))
    collection.find(query).cursor[Challenge].collect[List]()
  }

  def getChallengeInvitesForUser(user: User): Future[List[Challenge]] = {
    val query = Json.obj("members" -> Json.obj("$elemMatch" -> Json.obj("id" -> user.id, "joined" -> false)))
    collection.find(query).cursor[Challenge].collect[List]()
  }

  def createChallenge(challenge: Challenge) = {
    collection.insert(challenge)
  }

  def updateChallenge(challenge: Challenge) = {
    collection.update(Json.obj("id"-> challenge.id), challenge)
  }


}
