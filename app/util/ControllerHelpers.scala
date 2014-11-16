package util

import play.api.mvc._
import models.User
import scala.concurrent.Future
import play.mvc.Http
import mongo.UserManager
import java.util.{Date, UUID}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsValue

/**
 * Created by kevinli on 10/13/14.
 */
trait ControllerHelpers extends Controller {
  private def getSessionToken(headers: Headers): Option[UUID] = {
    headers.get(Http.HeaderNames.AUTHORIZATION) flatMap { received =>
      if (received.startsWith("Bearer")) Some(UUID.fromString(received.replaceFirst("Bearer ", "")))
      else if (received.startsWith("Basic")) {
        Some(UUID.fromString(new String((new sun.misc.BASE64Decoder).decodeBuffer(received.replaceFirst("Basic ", "")), "UTF-8").replaceFirst(":", "")))
      } else None
    }
  }

  def AuthenticatedJson(f: (Request[JsValue], User) => Future[Result]) = Action.async(parse.json) { request =>
    getSessionToken(request.headers) match {
      case Some(session) =>
        UserManager.findUserBySession(session).flatMap {
          case Some(user) => if(user.session.get.expDate.before(new Date)) {
            Future.successful(Unauthorized)
          } else {
            f(request, user)
          }
          case None => Future.successful(Unauthorized)
        }
      case None => Future.successful(Unauthorized)
    }
  }

  def Authenticated(f: (Request[AnyContent], User) => Future[Result]) = Action.async { request =>
    getSessionToken(request.headers) match {
      case Some(session) =>
        UserManager.findUserBySession(session).flatMap {
          case Some(user) => if(user.session.get.expDate.before(new Date)) {
            Future.successful(Unauthorized)
          } else {
            f(request, user)
          }
          case None => Future.successful(Unauthorized)
        }
      case None => Future.successful(Unauthorized)
    }
  }
}
