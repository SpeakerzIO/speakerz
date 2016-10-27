package models

import play.api.Environment
import play.api.libs.json.Json

import scala.io.Source
import scala.util.Try

case class Speaker(id: String, nickname: String, name: String) {
  def toJson = Speaker.format.writes(this)
}

object Speaker {
  implicit val format = Json.format[Speaker]

  def findById(id: String)(implicit env: Environment): Option[Speaker] = {
    Try(Json.parse(Source.fromFile(env.getFile(s"conf/speakers/$id.json")).mkString).validate(format).asOpt).toOption.flatten
  }
}
