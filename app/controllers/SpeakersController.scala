package controllers

import java.io.{File, FileFilter}

import anorm.Success
import models.Speaker
import old.play.GoodOldPlayframework
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import utils.EnhancedAction

object SpeakersController extends Controller with GoodOldPlayframework {

  implicit val ec = httpRequestsContext

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
}
