package controllers

import old.play.GoodOldPlayframework
import play.api.mvc.Controller
import utils.UserAction

object AccountController extends Controller with GoodOldPlayframework {

  def edit = UserAction { req =>
    Ok(views.html.edit((req.user \ "email").as[String]))
  }
}
