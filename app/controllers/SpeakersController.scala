package controllers

import java.io.{File, FileFilter}
import java.net.URLEncoder
import java.util.Base64

import models.Speaker
import old.play.GoodOldPlayframework
import org.joda.time.DateTime
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

  def createProfileLink(emailOpt: Option[String]) = Action {
    emailOpt match {
      case None => BadRequest(Json.obj("error" -> "no email provided"))
      case Some(email) => {
        val encoder = Base64.getEncoder
        val newId = encoder.encodeToString(email.getBytes("UTF-8"))
        if (existingIds.contains(newId)) {
          Conflict(Json.obj("error" -> "duplicate id"))
        } else {
          val content = Speaker(
            id = newId,
            nickname = Some("Your nickname here"),
            name = Some("Your name here"),
            resume = Some(Json.obj(
              "en" -> "Your resume here",
              "fr" -> "Votre bio ici",
              "anotherLang" -> "..."
            )),
            avatarUrl = Some("Your avatar URL here"),
            websiteUrl = Some("Your website URL here"),
            twitterHandle = Some("Your twitter handle here"),
            githubHandle = Some("Your github handle here"),
            talks = Seq()
          ).toJson
          val message = URLEncoder.encode(s"Adding $email to speakerz.io", "UTF-8")
          val description = URLEncoder.encode(s"Adding $email to speakerz.io from @me at ${DateTime.now().toString("dd/MM/yyyy HH:mm:ss")}", "UTF-8")
          val encodedContent = URLEncoder.encode(Json.prettyPrint(content), "UTF-8")
          val pr = URLEncoder.encode("quick-pull", "UTF-8")
          val link = s"https://github.com/sebprunier/speakerz/new/master/conf/speakers?filename=$newId.json&message=$message&description=$description&commit-choice=$pr&value=$encodedContent"
          Ok(Json.obj("link" -> link, "profile" -> s"/speakers/$newId"))
        }
      }
    }
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
