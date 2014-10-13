package models

import play.api.libs.json.{JsPath, Reads, JsString, Writes}

/**
 * Created by kevinli on 9/23/14.
 */

sealed trait Visibility { def visibility: String }
object Visibility {
  case object Global extends Visibility { val visibility = "Global" }
  case object Friends extends Visibility { val visibility = "Friends" }
  case object Private extends Visibility { val visibility = "Private" }

  implicit val visibilityWrites: Writes[Visibility] = new Writes[Visibility] {
    def writes(visibility: Visibility) = JsString(visibility.visibility)
  }

  implicit val checkinReads: Reads[Visibility] = JsPath.read[String].map{
    case "Global" => Global
    case "Friends" => Friends
    case "Private" => Private
    case _ => Private
  }
}


