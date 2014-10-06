package models

import java.util.Date
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


/**
 * Created by kevinli on 9/23/14.
 */

sealed trait CheckinFrequency { def frequency: String }
object CheckinFrequency {
  case object None extends CheckinFrequency { val frequency = "None" }
  case object OnceHourly extends CheckinFrequency { val frequency = "OnceHourly" }
  case object OnceDaily extends CheckinFrequency { val frequency = "OnceDaily" }
  case object OnceWeekly extends CheckinFrequency { val frequency = "OnceWeekly" }

  implicit val checkinWrites: Writes[CheckinFrequency] = new Writes[CheckinFrequency] {
    def writes(freq: CheckinFrequency) = JsString(freq.frequency)
  }

  implicit val checkinReads: Reads[CheckinFrequency] = JsPath.read[String].map{
    case "OnceHourly" => OnceHourly
    case "OnceDaily" => OnceDaily
    case "OnceWeekly" => OnceWeekly
    case _ => None
  }
}


sealed trait SprintLength{ def unit: String }
object SprintLength {
  case object Day extends SprintLength{ val unit = "Day" }
  case object Week extends SprintLength{ val unit = "Week" }
  case object Month extends SprintLength{ val unit = "Month" }

  implicit val sprintLengthWrites: Writes[SprintLength] = new Writes[SprintLength] {
    def writes(length: SprintLength) = JsString(length.unit)
  }

  implicit val sprintLengthReads: Reads[SprintLength] = JsPath.read[String].map{
    case "Day" => Day
    case "Week" => Week
    case "Month" => Month
    case _ => Day
  }
}

case class Rule(
  timesPerSprint: Int,
  sprintLength: SprintLength,
  numberOfSprints: Int,
  checkinFrequency: CheckinFrequency) {

  def getEndDate(startDate: Date) = {
    new Date //TODO
  }
}

object Rule {
  implicit val ruleFormatter = Json.format[Rule]
}

