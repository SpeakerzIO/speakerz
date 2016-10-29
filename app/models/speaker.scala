package models

import akka.stream.Materializer
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import com.google.common.io.Files
import org.pegdown.PegDownProcessor
import play.api.Environment
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

case class Speaker(
                    id: String,
                    nickname: String,
                    name: String,
                    resume: Option[JsObject],
                    avatarUrl: Option[String],
                    websiteUrl: Option[String],
                    twitterHandle: Option[String],
                    githubHandle: Option[String]
                  ) {

  def toJson = Speaker.format.writes(this)

  def resume(lang: String): String = {
    resume match {
      case None => "--"
      case Some(r) => {
        (r \ lang).asOpt[String]
          .orElse((r \ "fr").asOpt[String])
          .orElse((r \ "en").asOpt[String])
          .getOrElse("--")
      }
    }
  }

  def resumeToHtml(lang: String): String = {
    new PegDownProcessor().markdownToHtml(resume(lang))
  }
}

object Speaker {

  implicit val format = Json.format[Speaker]

  def findById(id: String)(implicit env: Environment, ec: ExecutionContext, materializer: Materializer): Future[Option[Speaker]] = {
    Try(env.getFile(s"conf/speakers/$id.json")) match {
      case Success(file) if file.exists() => {
        val source = StreamConverters.fromInputStream(() => Files.asByteSource(file).openStream())
        source.runFold(ByteString.empty)((a, b) => a.concat(b))
          .map(_.utf8String)
          .map(Json.parse)
          .map(_.validate(format).asOpt)
      }
      case _ => Future.successful(None)
    }
  }
}
