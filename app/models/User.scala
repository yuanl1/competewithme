package models

import java.util.UUID
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by kevinli on 9/23/14.
 */

trait User {
  val id: UUID
  val name: String
  val email: String
  val wins: Int
  val attempts: Int
}

case class DBUser (
  id : UUID = UUID.randomUUID(),
  name : String,
  password : String,
  email : String,
  wins : Int = 0,
  attempts : Int = 0) extends User

object DBUser {
  implicit val dbUserFormat = Json.format[DBUser]

  //For creating DBUsers from a new user
  val newUserReads: Reads[DBUser] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "password").read[String] and
    (JsPath \ "email").read[String]
  )(createDbUser _)

  private def createDbUser(name: String, password: String, email: String): DBUser = {
    DBUser(name = name, password = password, email = email)
  }
}

case class UserJson(
  id: UUID,
  name: String,
  email: String,
  wins: Int,
  attempts: Int) extends User

object UserJson {
  implicit val userJsonFormat = Json.format[UserJson]
}