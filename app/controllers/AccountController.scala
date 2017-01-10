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

  def destroyAccount = UserAction.async { ctx =>
    val id = Id.fromEmail(ctx.user.email)
    Speaker.findById(id).flatMap {
      case Some(speaker) => speaker.delete().map { _ =>
        // TODO : call auth0 here 
        Redirect(routes.SpeakersController.home()).withNewSession
      }
      case None => {
        Future.successful(Redirect(routes.SpeakersController.home()).withNewSession)
      }
    }
  }

  def edit = UserAction.async { ctx =>
    val id = Id.fromEmail(ctx.user.email)
    Speaker.findById(id).map(_.getOrElse(Speaker(
      id = id,
      nickname = None,
      name = ctx.user.name,
      resume = None,
      avatarUrl = None,
      websiteUrl = None,
      twitterHandle = None,
      githubHandle = None,
      talks = Seq.empty[Talk]
    ))).map { speaker =>
      Ok(views.html.edit(ctx.user, speaker))
    }
  }

  def save = UserAction.async(parse.json) { ctx =>
    val payload = ctx.req.body.as[JsObject] ++ Json.obj(
      "email" -> ctx.user.email,
      "id" -> Id.fromEmail(ctx.user.email)
    )
    Logger.info(Json.prettyPrint(payload))
    Speaker(payload) match {
      case Some(speaker) => {
        speaker.save().map { speaker =>
          Redirect(routes.AccountController.edit())
        }
      }
      case None => {
        Future.successful(BadRequest(views.html.badFormat(ctx.user)))
      }
    }
  }
}
