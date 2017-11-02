package controllers

import javax.inject.{Inject, Singleton}


import play.api.mvc.{AbstractController, ControllerComponents}


import scala.concurrent.ExecutionContext.Implicits.global
import services.JobAdService
import models.JobAdView


@Singleton
class JobController @Inject()(cc: ControllerComponents, jobAdService: JobAdService)
  extends AbstractController(cc){

  def getAllJobAds (site: String) = Action.async {

    val lists : scala.concurrent.Future[List[JobAdView]] = jobAdService.getAllJobAdViews(site)

    lists map {
      list =>  Ok(views.html.jobs( jobAdService.getMsg(),list))
    }
  }
}
