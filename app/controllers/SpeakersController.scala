package controllers

import java.io.{File, FileFilter}
import java.net.URLEncoder
import java.util.Base64
import javax.inject._

import akka.stream.Materializer
import models.Speaker
import play.api.Environment
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class SpeakersController @Inject()()(implicit env: Environment, ec: ExecutionContext, materializer: Materializer) extends Controller {

  lazy val existingIds = {
    env.getFile("/conf/speakers").listFiles(new FileFilter {
      override def accept(pathname: File): Boolean = pathname.getName.endsWith(".json")
    }).toSeq.map(f => f.getName.replace(".json", ""))
  }

  def home = Action {
    Ok(views.html.home())
  }

  def createProfileLink(nicknameOpt: Option[String]) = Action {
    nicknameOpt match {
      case None => Ok(Json.obj())
      case Some(nickname) => {
        val newId = Base64.getEncoder.encodeToString(nickname.getBytes("UTF-8"))
        if (existingIds.contains(newId)) {
          Conflict(Json.obj("error" -> "duplicate id"))
        } else {
          val content = Json.obj(
            "id" -> newId,
            "nickname" -> nickname,
            "name" -> "Your name here"
          )
          val encodedContent = URLEncoder.encode(Json.prettyPrint(content), "UTF-8")
          val link = s"https://github.com/sebprunier/speakerz/new/master/conf/speakers?filename=$newId.json&value=$encodedContent"
          Ok(Json.obj("link" -> link, "profile" -> s"/speakers/$newId"))
        }
      }
    }
  }

  def profile(id: String) = Action.async { req =>
    Speaker.findById(id).map {
      case Some(speaker) => {
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
