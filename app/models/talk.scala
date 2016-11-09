package models

import play.api.libs.json.{JsObject, Json}

case class Location(
                   lat: Double,
                   lon: Double
                   )

object Location {
  implicit val format = Json.format[Location]
}

case class Place(
                name: String,
                location: Option[Location]
                )

object Place {
  implicit val format = Json.format[Place]
}

case class Session(
                  name: String,
                  date: Option[String],
                  place: Option[Place],
                  links: Seq[String]
                  )

object Session {
  implicit val format = Json.format[Session]
}

case class Talk(
                 id: String,
                 name: JsObject,
                 `abstract`: Option[JsObject],
                 sessions: Seq[Session]
               ) {

  def name(lang: String): String = {
    (name \ lang).asOpt[String]
      .orElse((name \ "en").asOpt[String])
      .orElse((name \ "fr").asOpt[String])
      .getOrElse("--")
  }

  def `abstract`(lang: String): String = {
    `abstract` match {
      case None => "--"
      case Some(r) => {
        (r \ lang).asOpt[String]
          .orElse((r \ "en").asOpt[String])
          .orElse((r \ "fr").asOpt[String])
          .getOrElse("--")
      }
    }
  }
}

object Talk {

  implicit val format = Json.format[Talk]

}