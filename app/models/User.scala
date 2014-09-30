package models

import java.util.UUID

/**
 * Created by kevinli on 9/23/14.
 */

case class User(
  id : UUID,
  name : String,
  password : String,
  email : String
)

case class NewUser(
  name : String,
  password : String,
  email : String)

object NewUser {
  import play.api.libs.json.Json
  implicit val newUserFormat = Json.format[NewUser]
}

object User {
  import play.api.libs.json.Json
  implicit val userFormat = Json.format[User]

  def createUser(newUser: NewUser): User = {
    createUser(newUser.name, newUser.password, newUser.email)
  }

  def createUser(name: String, password: String, email: String) = {
    new User(
      id = UUID.randomUUID,
      name = name,
      password = password,
      email = email
    )
  }
}