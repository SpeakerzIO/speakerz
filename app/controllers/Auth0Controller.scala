package controllers

import old.play.GoodOldPlayframework
import play.api.Logger
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

case class Auth0Config(secret: String, clientId: String, callbackURL: String, domain: String)

object Auth0Config {
  lazy val config = Auth0Config(
    old.play.api.Play.configuration.getString("auth0.clientSecret").get,
    old.play.api.Play.configuration.getString("auth0.clientId").get,
    old.play.api.Play.configuration.getString("auth0.callbackURL").get,
    old.play.api.Play.configuration.getString("auth0.domain").get
  )
}

object Auth0Controller extends Controller with GoodOldPlayframework {

  implicit val ec = httpRequestsContext

  val logger = Logger("Auth0")

  def login(redirect: Option[String]) = Action { implicit request =>
    Ok(views.html.login(Auth0Config.config)).withSession(
      "redirect_to" -> redirect.getOrElse(routes.SpeakersController.home().url)
    )
  }

  def logout = Action { implicit request =>
    Ok(views.html.home()).removingFromSession(
      "userId",
      "userEmail",
      "redirect_to"
    )
  }

  def callback(codeOpt: Option[String] = None) = Action.async { implicit request =>
    codeOpt match {
      case None => Future.successful(BadRequest("No parameters supplied"))
      case Some(code) => getToken(code).flatMap { case (idToken, accessToken) =>
        getUser(accessToken).map { user =>
          val userId = (user \ "user_id").as[String]
          Cache.set(s"$userId-profile", user, Duration("1h"))
          logger.info(s"Login successful for admin user '${(user \ "email").as[String]}'")
          Redirect(request.session.get("redirect_to").getOrElse(routes.SpeakersController.home().url))
            .removingFromSession("redirect_to")
            .withSession(
              "userId" -> userId,
              "userEmail" -> (user \ "email").as[String]
            )
        }
      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }
  }

  def getToken(code: String): Future[(String, String)] = {
    val Auth0Config(clientSecret, clientId, callback, domain) = Auth0Config.config
    val tokenResponse = WS.url(s"https://$domain/oauth/token")
      .withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
      .post(
        Json.obj(
          "client_id" -> clientId,
          "client_secret" -> clientSecret,
          "redirect_uri" -> callback,
          "code" -> code,
          "grant_type"-> "authorization_code"
        )
      )
    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
        Future.successful((idToken, accessToken))
      }).getOrElse(Future.failed[(String, String)](new IllegalStateException("Tokens not sent")))
    }

  }

  def getUser(accessToken: String): Future[JsValue] = {
    val Auth0Config(_, _, _, domain) = Auth0Config.config
    val userResponse = WS.url(s"https://$domain/userinfo")
      .withQueryString("access_token" -> accessToken)
      .get()
    userResponse.flatMap(response => Future.successful(response.json))
  }
}