package models

import java.util.Date
import play.api.libs.json._
import org.joda.time.{Weeks, Days, Hours, DateTime}

// JSON library
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


sealed trait SprintLength{
  def unit: String
  def days: Int
}
object SprintLength {
  case object Day extends SprintLength{ val unit = "Day"; val days = 1}
  case object Week extends SprintLength{ val unit = "Week"; val days = 7 }
  case object Month extends SprintLength{ val unit = "Month"; val days = 30 }

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

  def canCheckin(checkins: Seq[Checkin]): Boolean = {
    checkins.headOption match {
      case Some(latestCheckin) =>
        checkinFrequency match {
          case CheckinFrequency.None => true
          case CheckinFrequency.OnceHourly =>
            Hours.hoursBetween(new DateTime(latestCheckin.date), DateTime.now).getHours >= 1
          case CheckinFrequency.OnceDaily =>
            Days.daysBetween(new DateTime(latestCheckin.date), DateTime.now).getDays >= 1
          case CheckinFrequency.OnceWeekly =>
            Weeks.weeksBetween(new DateTime(latestCheckin.date), DateTime.now).getWeeks >= 1
          case _ => false
        }
      case None => true
    }
  }

  def getEndDate(startDate: Date): Date = {
    new DateTime(startDate).plusDays(sprintLength.days * numberOfSprints).toDate
  }
}

object Rule {
  implicit val ruleFormatter = Json.format[Rule]
}

