package controllers

import java.io.{File, FileFilter}
import java.net.URLEncoder
import javax.inject._

import models.{Generators, Speaker}
import play.api.Environment
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class SpeakersController @Inject()()(implicit env: Environment) extends Controller {

  lazy val existingIds = {
    env.getFile("/conf").listFiles(new FileFilter {
      override def accept(pathname: File): Boolean = pathname.getName.endsWith(".json")
    }).toSeq.map(f => f.getName.replace(".json", ""))
  }

  def generate(ids: Seq[String]): String = {
    var id = Generators.token(8)
    while (ids.contains(id)) {
      id = Generators.token(8)
    }
    id
  }

  def home = Action {
    val newId = generate(existingIds)
    val content = Json.obj(
      "id" -> newId,
      "nickname" -> "Your-nickname-without-spaces here",
      "name" -> "Your name here"
    )
    val encodedContent = URLEncoder.encode(Json.prettyPrint(content), "UTF-8")
    val link = s"https://github.com/sebprunier/speakerz/new/master/conf/speakers?filename=$newId.json&value=$encodedContent"
    Ok(views.html.home(link))
  }

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
