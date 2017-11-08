package controllers

import javax.inject.{Inject, Singleton}

//import play.api.mvc.{AbstractController, ControllerComponents}
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import services.{CategoryService, CompanyService, JobAdService}
import models.{JobAdForm, JobAdView}
import play.api.libs.json.Writes
import play.api.libs.ws.{EmptyBody, WSClient}

@Singleton
class JobController @Inject()(cc: ControllerComponents, jobAdService: JobAdService, categoryService: CategoryService, companyService: CompanyService)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def getAllJobAds (site: String) = Action.async {

    val lists : scala.concurrent.Future[List[JobAdView]] = jobAdService.getAllJobAdViews(site)

    lists map {
      list =>  Ok(views.html.jobs( jobAdService.getMsg(),list))
    }
  }


  def index(site:String, id: Int) = Action.async{
    implicit request =>
    for {
        r1 <-categoryService.getAllCategoriesBySite(site)
        r2 <-companyService.getAllCompanies()
    }yield ( Ok(views.html.createjob(JobAdForm.createJobAdForm, site, id, r2, r1)))

  }


  def createJobAd(siteId: Int) = Action.async {
    implicit request => {
      val newJobAd = new JobAdView()

      JobAdForm.createJobAdForm.bindFromRequest.fold(
        error => BadRequest("ERROR"),
        jobAd => {
          newJobAd.title = jobAd.title
          if (jobAd.jobtype == "Basis Plus") {
            newJobAd.premium = Some(true)
          }
          if (jobAd.jobtype == "Recommended") {
            newJobAd.allow_personalized = true
          }
          newJobAd.externallink = jobAd.externallink
          newJobAd.startdate = jobAd.startdate
          newJobAd.enddate = jobAd.enddate
          newJobAd.category_id = jobAd.category_id
          newJobAd.company_id = jobAd.company_id
          newJobAd.site_id = siteId

        })

      val result: scala.concurrent.Future[Int] = jobAdService.createJobAd(newJobAd)

      result map {
        list => Redirect(routes.JobController.getAllJobAds("finanswatch.dk"))
      }
    }

  }

  def deleteJobAd(site: String, id: Int) = Action.async {

    val job : scala.concurrent.Future[String] = jobAdService.deleteJobAd(id)

    job map {
      list =>
        Redirect(routes.JobController.getAllJobAds(site))
    }
  }

}
