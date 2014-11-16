package models

import play.api.libs.json.{JsPath, Reads, JsString, Writes}

/**
 * Created by kli on 11/15/14.
 */
sealed trait Role { def role: String }
object Role {
  case object Owner extends Role { val role = "Owner" }
  case object Member extends Role { val role = "Member" }

  implicit val roleWrites: Writes[Role] = new Writes[Role] {
    def writes(role: Role) = JsString(role.role)
  }

  implicit val roleReads: Reads[Role] = JsPath.read[String].map{
    case "Owner" => Owner
    case "Member" => Member
    case _ => Member
  }
}