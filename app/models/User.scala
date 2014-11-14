package models

import _root_.util.PasswordHelper
import java.util.UUID
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by kevinli on 9/23/14.
 */

case class User(
  id: UUID = UUID.randomUUID(),
  name: String,
  email: String,
  password: String,
  salt: String,
  session: Option[SessionToken]) {

  def updateSession(session: SessionToken): User = {
    this.copy(session = Some(session))
  }
}

object User {
  val userDbFormat = Json.format[User]

  val newUserReads: Reads[User] = (
    (__ \ "name").read[String] and
    (__ \ "password").read[String] and
    (__ \ "email").read[String]
  )(createDbUser _)

  val userWrites = new Writes[User] {
    override def writes(obj: User): JsValue = Json.obj(
      "name" -> obj.name,
      "email" -> obj.email
    )
  }

  private def createDbUser(name: String, password: String, email: String): User = {
    val (hash, salt) = PasswordHelper.encryptPassword(password)
    User(name = name, password = hash, salt = salt, email = email, session = None)
  }
}