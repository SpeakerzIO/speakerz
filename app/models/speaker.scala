package models

import anorm.{SQL, SqlParser}
import old.play.api.libs.db.DB
import play.api.libs.json.{JsObject, JsValue, Json}
import utils.Id
import scala.util.Try
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

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

  def save()(implicit ec: ExecutionContext): Future[Speaker] = {
    Speaker.findById(id).flatMap {
      case Some(_) => Speaker.update(this).map(_ => this)
      case None => Speaker.insert(this).map(_ => this)
    }
  }

  def delete()(implicit ec: ExecutionContext): Future[Speaker] = {
    Speaker.delete(this.id).map(_ => this)
  }
}

object Speaker {

  implicit val format = Json.format[Speaker]

  def apply(json: JsValue): Option[Speaker] = format.reads(json).asOpt

  def findById(id: String)(implicit ec: ExecutionContext): Future[Option[Speaker]] = {
    Future.successful(
      DB.withConnection { implicit c =>
        SQL("select document::text from Speakerz where id = {id}")
          .on("id" -> id)
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
            .on("id" -> speaker.id, "document" -> Json.stringify(speaker.toJson))
            .executeInsert()
        ()
      }
    }
  }

  def update(speaker: Speaker)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      DB.withConnection { implicit c =>
        Logger.info(s"Update speaker $speaker")
        SQL("update Speakerz set document = {document}::json where id = {id}")
          .on("id" -> speaker.id, "document" -> Json.stringify(speaker.toJson))
          .executeUpdate()
        ()
      }
    }
  }

  def delete(id: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      DB.withConnection { implicit c =>
        SQL("delete from Speakerz where id = {id}")
          .on("id" -> id)
          .executeUpdate()
        ()
      }
    }
  }
}
