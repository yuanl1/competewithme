package mongo

import play.api.Play.current
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import models.Checkin
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import play.api.libs.json.Json
import scala.concurrent.Future

/**
 * Created by kevinli on 10/11/14.
 */
object CheckinManager {
  val db = ReactiveMongoPlugin.db
  val collection: JSONCollection = db.collection[JSONCollection]("checkins")

  def init() {
    collection.indexesManager.ensure(new Index(Seq(("user", IndexType.Ascending)), Some("user"), false, true))
    collection.indexesManager.ensure(new Index(Seq(("challenge", IndexType.Ascending)), Some("challenge"), false, true))
  }

  def create(checkin: Checkin) = {
    collection.insert(checkin)
  }

  def getByUser(user: UUID): Future[List[Checkin]] = {
    val query = Json.obj("user" -> user)
    collection.find(query).sort(Json.obj("date" -> -1)).cursor[Checkin].collect[List]()
  }

  def getByChallenge(challenge: UUID): Future[List[Checkin]] = {
    val query = Json.obj("challenge" -> challenge)
    collection.find(query).sort(Json.obj("date" -> -1)).cursor[Checkin].collect[List]()
  }

  def get(user: UUID, challenge: UUID): Future[List[Checkin]] = {
    val query = Json.obj("user" -> user, "challenge" -> challenge)
    collection.find(query).sort(Json.obj("date" -> -1)).cursor[Checkin].collect[List]()
  }
}
