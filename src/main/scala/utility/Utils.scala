package utility

import java.security.MessageDigest
import scala.util.Random
import java.text.SimpleDateFormat

object Utils {

  def validateEmailAddress(input: String): Boolean = {
    val emailRegex =
      "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$".r

    emailRegex.matches(input.trim.toUpperCase)
  }
  def cleanEmailAddress(email: String): String = {
    val normalized = email.trim.toLowerCase

    val parts = normalized.split("@")
    if (parts.length != 2) return normalized

    val (local, domain) = (parts(0), parts(1))

    domain match {
      case "gmail.com" | "googlemail.com" =>
        val cleanedLocal =
          local
            .takeWhile(_ != '+') // remove +suffix
            .replace(".", "") // remove dots

        s"$cleanedLocal@gmail.com"

      case _ =>
        normalized
    }
  }

  val simpleDateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def randomSalt(length: Int = 10): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    val sb = new StringBuilder
    for (_ <- 1 to length) {
      val randomNum = Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString()
  }

  def generatePasswordHash(salt: String, password: String): String = {
    val saltedPassword = s"$salt$password"
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(saltedPassword.getBytes)
    digest.map("%02x".format(_)).mkString
  }
}
