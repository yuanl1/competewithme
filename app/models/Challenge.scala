package models

import java.util.{Date, UUID}
import models.Visibility.Visibility

/**
 * Created by kevinli on 9/23/14.
 */

case class MemberProgress(
  member_id : UUID,
  progress :  Map[Date, UUID])


case class Challenge(
  id : UUID,
  name : String,
  start_date : Date,
  end_date : Date,
  reward: String,
  visibility: Visibility,
  members: Set[MemberProgress],
  rule: Rule)
