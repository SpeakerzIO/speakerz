package controllers

import models.{Speaker, Talk}
import old.play.GoodOldPlayframework
import play.api.mvc.Controller
import utils.{Id, UserAction}
import scala.concurrent.Future
import play.api.libs.json.{Json, JsObject}

import play.api.Logger

object AccountController extends Controller with GoodOldPlayframework {

  implicit val ec = httpRequestsContext

  def edit = UserAction.async { req =>
    val id = Id.fromEmail(req.user.email)
    Speaker.findById(id).map(_.getOrElse(Speaker(
      id = id,
      nickname = None,
      name = req.user.name,
      resume = None,
      avatarUrl = None,
      websiteUrl = None,
      twitterHandle = None,
      githubHandle = None,
      talks = Seq.empty[Talk]
    ))).map { speaker =>
      Ok(views.html.edit(req.user, speaker))
    }
  }

  def save = UserAction.async(parse.json) { req =>
    val payload = req.req.body.as[JsObject] ++ Json.obj(
      "email" -> req.user.email,
      "id" -> Id.fromEmail(req.user.email)
    )
    Logger.info(Json.prettyPrint(payload))
    Speaker(payload) match {
      case Some(speaker) => {
        speaker.save().map { speaker =>
          Redirect(routes.AccountController.edit())
        }
      }
      case None => {
        Future.successful(BadRequest(views.html.badFormat(req.user)))
      }
    }
  }
}
