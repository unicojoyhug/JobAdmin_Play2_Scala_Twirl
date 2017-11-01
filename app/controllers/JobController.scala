package controllers

import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Logger}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import scala.concurrent.ExecutionContext.Implicits.global
import services.{CategoryService, CompanyService, JobAdService}
import models.{Category, Company, JobAd, JobAdView}
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future


@Singleton
class JobController @Inject()(cc: ControllerComponents, ws: WSClient, jobAdService: JobAdService, companyService: CompanyService, categoryService: CategoryService, config: Configuration)
  extends AbstractController(cc){

  def getAllJobAds (site: String) = Action.async {

    //val lists : scala.concurrent.Future[List[JobAd]] = jobAdService.getAllJobs(config, site)
    val lists : scala.concurrent.Future[List[JobAdView]] = getAllJobAdViews(config, site)

    lists map {
      list =>  Ok(views.html.jobs( jobAdService.getMsg(),list))
    }

  }

  def getAllJobAdViews (config: Configuration, site: String): Future[List[JobAdView]] = {

    for {
      r1 <- jobAdService.getAllJobs(config, site)
      r2 <- companyService.getAllCompanies(config)
      r3 <- categoryService.getAllCategoriesBySite(config, site)
    } yield jobAdService.convertDomainToViewModel(r1, r2, r3)

  }

}
