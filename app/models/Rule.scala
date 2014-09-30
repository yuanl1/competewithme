package models

import models.Frequency.Frequency
import models.UnitOfTime.UnitOfTime


/**
 * Created by kevinli on 9/23/14.
 */

object Frequency extends Enumeration {
  type Frequency = Value
  val None, OnceHourly, OnceDaily, OnceWeekly = Value
}

object UnitOfTime extends Enumeration {
  type UnitOfTime = Value
  val Day, Week, Month = Value
}

case class Rule(
  goal: Int,
  frequency: Frequency,
  timeUnit : UnitOfTime,
  repetitions: Int) {
}

