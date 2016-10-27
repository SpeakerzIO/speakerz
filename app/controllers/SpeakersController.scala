package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class SpeakersController @Inject() extends Controller {

  def profile(id: String) = Action { req =>
    req.headers.get("Accept") match {
      case Some(accept) => accept.split(",").toSeq.headOption match {
        case Some("text/html") => Ok(views.html.speaker(id))
        case e => Ok(Json.obj("id" -> id, "accept" -> e))
      }
      case None => Ok(Json.obj("id" -> id))
    }

  }

}
