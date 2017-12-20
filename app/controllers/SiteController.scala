package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import services.SiteService
import models.Site


@Singleton
class SiteController @Inject()(cc: ControllerComponents, siteService: SiteService)
  extends AbstractController(cc){


  def getAllSites = Action.async {
    val lists : scala.concurrent.Future[List[Site]] = siteService.getAllSites()
    lists map {
      list =>  Ok(views.html.sites(siteService.getMsg(), list))
    }
  }

  def selectSite(siteId: Int) = Action {

    Ok((s"Selected Id = $siteId"))
  }
}
