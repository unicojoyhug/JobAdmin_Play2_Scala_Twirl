package controllers

import javax.inject.{Inject, Singleton}

import models.JobAd
import play.filters.csrf.CSRF

//import play.api.mvc.{AbstractController, ControllerComponents}
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import services.{CategoryService, CompanyService, JobAdService}
import models.{JobAdForm, JobAdView}
import play.api.libs.json.Writes
import play.api.libs.ws.{EmptyBody, WSClient}
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

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
   // }yield ( Ok(views.html.createjob(JobAdForm.createJobAdForm, site, id, r2, r1)))
     }yield ( Ok(views.html.createjob2(site, id, r2, r1)))
    //}yield ( Ok(views.html.createjob3()))

  }


  def createJobAd(siteId: Int) = Action.async{
    request =>
      val param = request.body.asFormUrlEncoded
      var jobAdView = new JobAdView()
      jobAdView.title = param.get("title")(0)

      val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
      jobAdView.startdate = formatter.parseDateTime(param.get("startdate")(0)).getMillis
      jobAdView.enddate = formatter.parseDateTime(param.get("enddate")(0)).getMillis

      jobAdView.site_id = siteId
      jobAdView.externallink = param.get("externallink")(0)
      jobAdView.company_id = param.get("company_id")(0).toInt
      jobAdView.category_id = param.get("category_id")(0) match {
        case id: String => Try(id.toInt) toOption
        case _ => None
      }

      val jobtype = param.get("jobtype")(0)
      if(jobtype =="basis_plus"){
        jobAdView.premium = Option(true)
      }else if(jobtype =="recommended"){
        jobAdView.allow_personalized = true
      }

      val newJobAdId: scala.concurrent.Future[Int] = jobAdService.createJobAd(jobAdView)

      newJobAdId map {
        id =>
          Logger.debug("New JobAd id = "+id)
          Redirect(routes.FileUploadController.upload(id))
          Redirect(routes.SiteController.getAllSites())
    }

  }


  def deleteJobAd(site: String, id: Int) = Action.async {

    val job : scala.concurrent.Future[String] = jobAdService.deleteJobAd(id)

    job map {
      list =>
        Redirect(routes.JobController.getAllJobAds(site))
    }
  }

  def editIndex(site: String, jobId: Int) = Action.async{
    implicit request =>
      val param = request.body.asFormUrlEncoded
      var jobAdView = new JobAdView()

      jobAdView.id = jobId
      jobAdView.title = param.get("title")(0)
      jobAdView.logo = param.get("logo")(0) match {
        case id: String => Try(id.toString) toOption
        case _ => None
      }

      jobAdView.startdate = param.get("startdate")(0).filterNot((x: Char) => x.isWhitespace).toLong
      jobAdView.enddate = param.get("enddate")(0).filterNot((x: Char) => x.isWhitespace).toLong

      jobAdView.site_id = param.get("site_id")(0).toInt
      jobAdView.site_name = site
      jobAdView.externallink = param.get("externallink")(0)
      jobAdView.company_id = param.get("company_id")(0).toInt
      jobAdView.category_id = param.get("category_id")(0) match {
        case id: String => Try(id.toInt) toOption
        case _ => None
      }

      jobAdView.premium = param.get("premium")(0) match {
        case s: String => Try(s.toBoolean) toOption
        case _ => None
      }
      jobAdView.allow_personalized = param.get("allow_personalized")(0).toBoolean

      for {
        r1 <-categoryService.getAllCategoriesBySite(site)
        r2 <-companyService.getAllCompanies()
      }yield ( Ok(views.html.editjob(jobAdView, r2, r1)))

  }

  def editJobAd(site: String, siteId: Int, id: Int) = Action.async{
    request =>
      val param = request.body.asFormUrlEncoded
      var jobAdView = new JobAdView()

      jobAdView.id = id
      jobAdView.title = param.get("title")(0)
      jobAdView.logo = param.get("logo")(0) match {
        case id: String => Try(id.toString) toOption
        case _ => None
      }

      val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
      jobAdView.startdate = formatter.parseDateTime(param.get("startdate")(0).filterNot((x: Char) => x.isWhitespace)).getMillis
      jobAdView.enddate = formatter.parseDateTime(param.get("enddate")(0).filterNot((x: Char) => x.isWhitespace)).getMillis

      jobAdView.site_id = siteId
      jobAdView.externallink = param.get("externallink")(0)
      jobAdView.company_id = param.get("company_id")(0).toInt
      jobAdView.category_id = param.get("category_id")(0) match {
        case id: String => Try(id.toInt) toOption
        case _ => None
      }

      val jobtype = param.get("jobtype")(0)
      if(jobtype =="basis_plus"){
        jobAdView.premium = Option(true)
      }else if(jobtype =="recommended"){
        jobAdView.allow_personalized = true
      }

      val newJobAdId: scala.concurrent.Future[Int] = jobAdService.editJobAd(jobAdView)

      newJobAdId map {
        id =>
          Redirect(routes.SiteController.getAllSites())
      }

  }

}


