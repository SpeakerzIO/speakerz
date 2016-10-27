package controllers

import javax.inject._

import models.Speaker
import play.api.Environment
import play.api.mvc._

@Singleton
class SpeakersController @Inject()()(implicit env: Environment) extends Controller {

  def profile(id: String, version: String, name: String) = Action { req =>
    Speaker.findById(id) match {
      case Some(speaker) if speaker.nickname == name => {
        req.headers.get("Accept") match {
          case Some(accept) => accept.split(",").toSeq.headOption match {
            case Some("text/html") => Ok(views.html.speaker(speaker))
            case e => Ok(speaker.toJson)
          }
          case None => Ok(speaker.toJson)
        }
      }
      case _ => NotFound("Speaker not found")
    }
  }

}
