/**
 * Created by kevinli on 9/24/14.
 */

import play.api._
import mongo.{CheckinManager, ChallengeManager, UserManager}


object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    Logger.info("Initializing User Manager")
    UserManager.init()
    ChallengeManager.init()
    CheckinManager.init()
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}
