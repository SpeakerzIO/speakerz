package controllers

import models.{Speaker, Talk}
import old.play.GoodOldPlayframework
import play.api.mvc.Controller
import utils.{Id, UserAction}

object AccountController extends Controller with GoodOldPlayframework {

  implicit val ec = httpRequestsContext

  def edit = UserAction.async { req =>
    val id = Id.fromEmail(req.user.email)
    Speaker.findById(Id.clean(id)).map(_.getOrElse(Speaker(
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
}
