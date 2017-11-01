package controllers

import javax.inject.{Inject, Singleton}

import ch.qos.logback.core.net.SyslogOutputStream
import play.api.{Configuration, Logger}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import scala.concurrent.ExecutionContext.Implicits.global
import services.SiteService
import models.Site


@Singleton
class SiteController @Inject()(cc: ControllerComponents, ws: WSClient, siteService: SiteService, config: Configuration)
  extends AbstractController(cc){


  def getAllSites = Action.async {
    val lists : scala.concurrent.Future[List[Site]] = siteService.getAllSites(config)
    lists map {
      list =>  Ok(views.html.sites( siteService.getMsg(), list))
    }
  }

  def selectSite(siteId: Int) = Action {

    Ok((s"Selected Id = $siteId"))
  }
}
