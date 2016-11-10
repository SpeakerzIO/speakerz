package controllers

import java.io.{File, FileFilter}

import models.Speaker
import old.play.GoodOldPlayframework
import play.api.libs.json.Json
import play.api.mvc._
import utils.EnhancedAction

object SpeakersController extends Controller with GoodOldPlayframework {

  implicit val ec = httpRequestsContext
  implicit val env = Environment
  implicit val mat = defaultMaterializer

  lazy val existingIds = {
    Environment.getFile("/conf/speakers").listFiles(new FileFilter {
      override def accept(pathname: File): Boolean = pathname.getName.endsWith(".json")
    }).toSeq.map(f => f.getName.replace(".json", ""))
  }

  def home = EnhancedAction { ctx =>
    Ok(views.html.home(ctx.user))
  }

  def developers = EnhancedAction { ctx =>
    Ok(views.html.developers(ctx.user))
  }

  def profile(id: String) = EnhancedAction.async { req =>
    Speaker.findById(id).map {
      case Some(speaker) if req.acceptsHtml => Ok(views.html.speaker(speaker, req.lang, req.user))
      case Some(speaker) => Ok(speaker.toJson)
      case None if req.acceptsHtml => Ok(views.html.notfound(req.user))
      case None => NotFound("Not found")
    }
  }

  def talks(id: String) = EnhancedAction.async { req =>
    Speaker.findById(id).map {
      case Some(speaker) if req.acceptsHtml => Ok(views.html.talks(speaker, req.lang, req.user))
      case Some(speaker) => Ok((speaker.toJson \ "talks").getOrElse(Json.arr()))
      case None if req.acceptsHtml => Ok(views.html.notfound(req.user))
      case None => NotFound("Not found")
    }
  }

  def talk(id: String, talkId: String) = EnhancedAction.async { req =>
    Speaker.findById(id).map(_.flatMap(speaker => speaker.talk(talkId).map(talk => (speaker, talk)))).map {
      case Some((speaker, talk)) if req.acceptsHtml => Ok(views.html.talk(speaker, talk, req.lang, req.user))
      case Some((speaker, talk)) => Ok(talk.toJson)
      case None if req.acceptsHtml => Ok(views.html.notfound(req.user))
      case None => NotFound("Not found")
    }
  }

  private def lang(req: Request[AnyContent]): String = {
    req.headers.get("Accept-Language") flatMap { h =>
      h.split(",").toSeq.map(l => l.split(";").toSeq.headOption).headOption.flatten.flatMap(i => i.split("-").toSeq.headOption)
    } getOrElse "en"
  }
}
