package controllers

import models.{Speaker, Talk}
import old.play.GoodOldPlayframework
import play.api.mvc.Controller
import utils.{Id, UserAction}
import scala.concurrent.Future
import play.api.libs.json._

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
    Speaker(payload) match {
      case JsError(e) => {
        Logger.debug(s"Error while reading payload $e");
        Future.successful(BadRequest(Json.obj(
          "error" -> e.map { t =>
            Json.obj("path" -> t._1.toString, "errors" -> JsArray(t._2.map(e => JsString(e.message))))
          }
        )))
      }
      case JsSuccess(speaker, _) => speaker.save().map { speaker =>
        Redirect(routes.AccountController.edit())
      }
    }
  }
}
