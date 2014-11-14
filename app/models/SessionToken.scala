package models

import java.util.UUID
import java.util.Date
import org.joda.time.DateTime
import play.api.libs.json.Json

/**
 * Created by kli on 11/14/14.
 */
case class SessionToken(id: UUID, issueDate: Date, expDate: Date)
object SessionToken {
  implicit val userDbFormat = Json.format[SessionToken]
  private val TOKEN_EXP_DAYS = 30
  def create() = {
    val now = DateTime.now
    SessionToken(UUID.randomUUID, now.toDate, now.plusDays(TOKEN_EXP_DAYS).toDate)
  }
}
