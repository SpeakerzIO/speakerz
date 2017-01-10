package utils

import java.security._
import java.math._

object Id {

  // echo 'mail@mail.com | sed 's/\+.*@/@/g' | md5'
  def fromEmail(email: String): String = {
    val s = email.replaceAll("""\+.*@""", "@")
    val m = MessageDigest.getInstance("MD5")
    m.update(s.getBytes(), 0, s.length())
    new BigInteger(1, m.digest()).toString(16);
  }
}
