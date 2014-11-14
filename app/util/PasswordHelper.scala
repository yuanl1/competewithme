package util

import org.mindrot.jbcrypt.BCrypt

object PasswordHelper {

  private final val Rounds = 10

  def encryptPassword(password: String): (String, String) = {
    val salt = BCrypt.gensalt(Rounds)
    val hash = BCrypt.hashpw(password, salt)
    (hash, salt)
  }

  def matchPassword(password: String, hash: String): Boolean = {
    BCrypt.checkpw(password, hash)
  }
}
