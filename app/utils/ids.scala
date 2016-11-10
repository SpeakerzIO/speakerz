package utils

import java.util.Base64

object Id {

  def fromEmail(email: String): String = {
    Base64.getEncoder.encodeToString(email.getBytes("UTF-8"))
  }

  def clean(id: String): String = {
    val email: String = new String(Base64.getDecoder.decode(id.getBytes("UTF-8")), "UTF-8")
    fromEmail(email.replaceAll("""\+.*@""", "@"))
  }

}