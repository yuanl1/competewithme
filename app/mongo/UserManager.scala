package mongo

import play.api.Play.current
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import models.{UserJson, DBUser}
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import play.api.libs.json.Json
import scala.concurrent.Future


/**
 * Created by kevinli on 9/29/14.
 */
object UserManager {
  val db = ReactiveMongoPlugin.db
  val collection: JSONCollection = db.collection[JSONCollection]("users")

  def init() {
    collection.indexesManager.ensure(new Index(Seq(("id", IndexType.Ascending)), Some("id"), true, true))
  }

  def saveUser(newUser: DBUser) = {
    collection.save(newUser)
  }

  def getUser(id: UUID): Future[Option[UserJson]] = {
    collection.find(Json.obj("id" -> id)).one[UserJson]
  }

  def getUsers(): Future[List[UserJson]] = {
    collection.find(Json.obj()).cursor[UserJson].collect[List]()
  }

}
