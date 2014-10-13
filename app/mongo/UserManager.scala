package mongo

import play.api.Play.current
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import models.{Challenge, User}
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
    collection.indexesManager.ensure(new Index(Seq(("email", IndexType.Ascending)), Some("email"), true, true))
  }

  def createUser(newUser: User) = {
    collection.insert(newUser)
  }

  def getUser(id: UUID): Future[Option[User]] = {
    collection.find(Json.obj("id" -> id)).one[User]
  }

  def getUsers(): Future[List[User]] = {
    collection.find(Json.obj()).cursor[User].collect[List]()
  }

  def getUsersInChallenge(challenge: Challenge): Future[List[User]] = {
    collection.find(Json.obj("id" -> Json.obj("$in" -> challenge.members))).cursor[User].collect[List]()
  }

  def getPendingUsersInChallenge(challenge: Challenge): Future[List[User]] = {
    collection.find(Json.obj("id" -> Json.obj("$in" -> challenge.pendingMembers))).cursor[User].collect[List]()
  }

}
