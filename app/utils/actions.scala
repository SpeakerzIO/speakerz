package utils

import java.net.URLEncoder

import models.User
import old.play.GoodOldPlayframework
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.{ActionBuilder, Request, Result, Results}

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Success

case class UserRequest[A](req: Request[A], user: User)

object UserAction extends ActionBuilder[UserRequest] with GoodOldPlayframework {

  implicit val ec = httpRequestsContext
  val logger = Logger("UserAction")

  def getUserFromAuth0Api(id: String): Future[Option[User]] = {
    val encodedId = URLEncoder.encode(id, "UTF-8")
    val domain = Configuration.getString("auth0.domain").getOrElse("-")
    val bearer = Configuration.getString("auth0.apiBearerRead").getOrElse("-")
    WS.url(s"https://$domain/api/v2/users/$encodedId").withHeaders(
      "Authorization" -> s"Bearer $bearer"
    ).get().flatMap {
      case response if response.status == 200 =>
        logger.info(s"Profile successfully fetched from Auth0 instead of cache")
        Future.successful(Some(User(response.json)))
      case _ =>
        logger.info(s"Failed to fetch profile from Auth0 instead of cache")
        Future.successful(None)
    }
  }

  def user[A](request: Request[A]): Future[Option[User]] = {
    Future.successful(request.session.get("userId")).flatMap {
      case None => Future.successful(None)
      case Some(id) =>
        Cache.get[JsValue](s"$id-profile") match {
          case Some(profile) => Future.successful(Some(User(profile)))
          case None => getUserFromAuth0Api(id).andThen {
            case Success(Some(profile)) => Cache.set(s"$id-profile", profile, Duration("1h"))
          }
        }
    }
  }

  override def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    user(request) flatMap {
      case None =>
        logger.info(s"Access refused to '${request.uri}' for user with unknown email")
        Future.successful(Results.Redirect(controllers.routes.Auth0Controller.login(Some(request.uri))))
      case Some(profile) =>
        // logger.info(Json.prettyPrint(profile))
        logger.info(s"Access granted to '${request.uri}' for admin user with '${profile.email}'")
        block(UserRequest[A](request, profile))
    }
  }
}

case class EnhancedRequest[A](req: Request[A], accept: String, lang: String, user: Option[User]) {
  def acceptsHtml = accept == "text/html"
}

object EnhancedAction extends ActionBuilder[EnhancedRequest] with GoodOldPlayframework {

  override def invokeBlock[A](request: Request[A], block: (EnhancedRequest[A]) => Future[Result]): Future[Result] = {

    implicit val ec = httpRequestsContext

    val accept = request.headers.get("Accept").flatMap(_.split(",").toSeq.headOption).getOrElse("application/json")
    val lang = request.headers.get("Accept-Language") flatMap { h =>
      h.split(",").toSeq.map(l => l.split(";").toSeq.headOption).headOption.flatten.flatMap(i => i.split("-").toSeq.headOption)
    } getOrElse "en"
    UserAction.user(request) flatMap { opt =>
      block(EnhancedRequest[A](request, accept, lang, opt))
    }
  }
}
