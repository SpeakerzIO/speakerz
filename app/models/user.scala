package models

import play.api.libs.json.JsValue

case class User(profile: JsValue) {
  def email: String = (profile \ "email").as[String]
}
