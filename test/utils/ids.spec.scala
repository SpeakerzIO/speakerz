package utils

import org.scalatestplus.play._

class IdSpec extends PlaySpec {

  "clean()" must {
    "remove +XXX in emails" in {
      val encodedEmail1 = Id.fromEmail("me@gmail.com")
      val encodedEmail2 = Id.fromEmail("me+123@gmail.com")
      Id.clean(encodedEmail2) mustBe encodedEmail1
    }
  }

}