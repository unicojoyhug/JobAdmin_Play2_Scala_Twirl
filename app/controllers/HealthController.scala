package controllers

import javax.inject.Inject

import models.Site
import play.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SiteService

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

class HealthController @Inject()(cc: ControllerComponents, siteService: SiteService)
  extends AbstractController(cc){


  def health = Action.async {
    request =>
    val lists = siteService.getAllSites()
    lists.map {
      list =>  Ok(Json.toJson(Map("Status" -> "OK", "Result" -> "Api Connected")))
    }.recover {
      case e =>
        Logger.error("Everything is not ok, check logs")
        InternalServerError(e.toString)
    }
  }
}
