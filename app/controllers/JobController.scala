package controllers

import javax.inject.{Inject, Singleton}

import play.api.{Configuration}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}


import scala.concurrent.ExecutionContext.Implicits.global
import services.{CategoryService, CompanyService, JobAdService}
import models.{JobAdView}

import scala.concurrent.Future


@Singleton
class JobController @Inject()(cc: ControllerComponents, ws: WSClient, jobAdService: JobAdService, companyService: CompanyService, categoryService: CategoryService, config: Configuration)
  extends AbstractController(cc){

  def getAllJobAds (site: String) = Action.async {

    val lists : scala.concurrent.Future[List[JobAdView]] = jobAdService.getAllJobAdViews(companyService, categoryService, config, site)

    lists map {
      list =>  Ok(views.html.jobs( jobAdService.getMsg(),list))
    }

  }

}
