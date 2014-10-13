package models

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
  wins: Int = 0,
  attempts: Int = 0)

object User {
  implicit val userDbFormat = Json.format[User]

  val newUserReads: Reads[User] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "password").read[String] and
    (JsPath \ "email").read[String]
  )(createDbUser _)

  private def createDbUser(name: String, password: String, email: String): User = {
    User(name = name, password = password, email = email)
  }

  val withoutPassword = (JsPath \ "password").json.prune
}