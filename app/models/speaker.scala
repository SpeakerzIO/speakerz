package models

import akka.stream.Materializer
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import anorm.{SQL, SqlParser}
import com.google.common.io.Files
import old.play.api.libs.db.DB
import play.api.{Environment, Logger}
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}
import utils.Id

case class Speaker(
                    id: String,
                    nickname: Option[String],
                    name: Option[String],
                    resume: Option[JsObject],
                    avatarUrl: Option[String],
                    websiteUrl: Option[String],
                    twitterHandle: Option[String],
                    githubHandle: Option[String],
                    talks: Seq[Talk]
                  ) {

  def toJson = Speaker.format.writes(this)

  def resume(lang: String): String = {
    resume match {
      case None => "--"
      case Some(r) => {
        (r \ lang).asOpt[String]
          .orElse((r \ "en").asOpt[String])
          .orElse((r \ "fr").asOpt[String])
          .getOrElse("--")
      }
    }
  }

  def talk(talkId: String): Option[Talk] = {
    talks.find(talk => talk.id == talkId)
  }
}

object Speaker {

  implicit val format = Json.format[Speaker]

  def findByIdFromFiles(id: String)(implicit env: Environment, ec: ExecutionContext, materializer: Materializer): Future[Option[Speaker]] = {
    Try(env.getFile(s"conf/speakers/${Id.clean(id)}.json")) match {
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

  def findById(id: String)(implicit ec: ExecutionContext): Future[Option[Speaker]] = {
    Future.successful(
      DB.withConnection { implicit c =>
        SQL("select document::text from Speakerz where id = {id}")
          .on("id" -> Id.clean(id))
          .as(SqlParser.str("document").singleOpt)
          .map(Json.parse)
          .map(format.reads)
          .filter(_.isSuccess)
          .map(_.get)
      }
    )
  }

  def insert(speaker: Speaker)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      DB.withConnection { implicit c =>
        SQL("insert into Speakerz (id, document) values ({id}, {document}::json)")
            .on("id" -> Id.clean(speaker.id), "document" -> Json.stringify(speaker.toJson))
            .executeInsert()
        ()
      }
    }
  }

  def update(speaker: Speaker)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      DB.withConnection { implicit c =>
        SQL("update Speakerz set document = {document}::json where id = {id}")
          .on("id" -> Id.clean(speaker.id), "document" -> Json.stringify(speaker.toJson))
          .executeUpdate()
        ()
      }
    }
  }
}
