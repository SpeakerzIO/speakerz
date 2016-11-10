package utils

import java.net.URLEncoder

import old.play.GoodOldPlayframework
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.{ActionBuilder, Request, Result, Results}

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Success

case class UserRequest[A](req: Request[A], user: JsObject)

object UserAction extends ActionBuilder[UserRequest] with GoodOldPlayframework {

  implicit val ec = httpRequestsContext
  val logger = Logger("AdminAction")

  def getUserFromAuth0Api(id: String): Future[Option[JsValue]] = {
    val encodedId = URLEncoder.encode(id, "UTF-8")
    val domain = Configuration.getString("auth0.domain").getOrElse("-")
    val bearer = Configuration.getString("auth0.apiBearer").getOrElse("-")
    WS.url(s"https://$domain/api/v2/users/$encodedId").withHeaders(
      "Authorization" -> s"Bearer $bearer"
    ).get().flatMap {
      case response if response.status == 200 =>
        logger.info(s"Profile successfully fetched from Auth0 instead of cache")
        Future.successful(Some(response.json))
      case _ =>
        logger.info(s"Failed to fetch profile from Auth0 instead of cache")
        Future.successful(None)
    }
  }

  override def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    Future.successful(request.session.get("userId")).flatMap {
      case None => Future.successful(None)
      case Some(id) =>
        Cache.get[JsValue](s"$id-profile") match {
          case Some(profile) => Future.successful(Some(profile))
          case None => getUserFromAuth0Api(id).andThen {
            case Success(Some(profile)) => Cache.set(s"$id-profile", profile, Duration("1h"))
          }
        }
    } flatMap {
      case None =>
        logger.info(s"Access refused to '${request.uri}' for user with unknown email")
        Future.successful(Results.Redirect(controllers.routes.Auth0Controller.login(Some(request.uri))))
      case Some(profile) =>
        // logger.info(Json.prettyPrint(profile))
        logger.info(s"Access granted to '${request.uri}' for admin user with '${(profile \ "email").as[String]}'")
        block(UserRequest[A](request, profile.as[JsObject]))
    }
  }
}
