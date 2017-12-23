package controllers

import java.io.File

import org.joda.time.format.DateTimeFormat
import javax.inject.{Inject, Singleton}

import play.api.mvc._
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}
import services.{CategoryService, CompanyService, FileService, JobAdService}
import models.{CategoryWithNumberOfJobsView, JobAdView}

import scala.concurrent.Future


@Singleton
class JobController @Inject()(cc: ControllerComponents, fileService: FileService, jobAdService: JobAdService, categoryService: CategoryService, companyService: CompanyService)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def getAllJobAds (site: String) = Action.async {
    for{
      list <- jobAdService.getAllJobAdViews(site)
      unexpiredJobAds <- jobAdService.getUnexpiredJobAdList(list)
      expiredJobAds <- jobAdService.getExpiredJobAdList(list)

    }yield Ok(views.html.jobs( jobAdService.getMsg(), list))

  }

  def showUnexpiredJobs (site: String) = Action.async {
    for{
      list <- jobAdService.getAllJobAdViews(site)
      unexpiredJobAds <- jobAdService.getUnexpiredJobAdList(list)
      activeJobAds <- countActiveJobAds(unexpiredJobAds)
      upcomingJobAds <- countUpcomingJobAds(unexpiredJobAds.size, activeJobAds)

    }yield Ok(views.html.unexpiredjobs( jobAdService.getMsg(),  unexpiredJobAds, activeJobAds, upcomingJobAds ))

  }

  def showExpiredJobs (site: String) = Action.async {
    for{
      list <- jobAdService.getAllJobAdViews(site)
      expiredJobAds <- jobAdService.getExpiredJobAdList(list)
    }yield Ok(views.html.expiredjobs( jobAdService.getMsg(),  expiredJobAds, expiredJobAds.size ))

  }


  private def countActiveJobAds(list: List[JobAdView]): Future[Int] = {
    Future.successful(list.count(j => j.startdate<DateTime.now().getMillis))
  }

  private def countUpcomingJobAds(total: Int, activeJobs: Int): Future[Int] ={
    Future.successful(total - activeJobs)
  }



  def index(site:String, id: Int)= Action.async{
    implicit request =>
      for {
        companyList <-companyService.getAllCompanies()
        categoryList <- getCategoryWithJobAdsNumberList(site)
      }yield Ok(views.html.createjob2(site, id, companyList, categoryList))
  }



  def getCategoryWithJobAdsNumberList(site:String): Future[List[CategoryWithNumberOfJobsView]] = {
    for{
      list <- jobAdService.getAllJobAdViews(site)
      joblist <- jobAdService.getUnexpiredJobAdList(list)
      categorylist <- categoryService.getAllCategoriesBySite(site)
    }yield categoryService.getCategoryWithNumberOfJobAdsBySite (joblist,site,categorylist)
  }


  def createJobAd(siteId: Int) = Action.async(parse.multipartFormData(fileService.handleFilePartAsFile)) { request =>

    val param = request.body.asFormUrlEncoded

    var jobAdView = getCommonDataFromView(Some(param), caseName = "jobAd", param.size, fileCheck=true)


    jobAdView.site_id = siteId

    val joblogoType = param.get("joblogo").head.head

    var result = for {
      newJobAdId <- jobAdService.createJobAd(jobAdView)
      uploadFileStatus <- uploadFile(request, newJobAdId, joblogoType)
    }yield uploadFileStatus

    result.map {
      res=>
        Redirect(routes.SiteController.getAllSites())
    }
  }

  private def uploadFile(request: Request[MultipartFormData[File]], jobAdId: Int, joblogoType: String): Future[Try[Int]] = {

    if (joblogoType == "pdf") {
      fileService.uploadFile(request.body.file("pdf"), jobAdId, caseName = "jobs")
    } else {
      Future.successful(Failure(new Exception("No file to upload")))
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
      var jobAdView = getCommonDataFromView(param, caseName = "editIndex", param.size, fileCheck=false)

      jobAdView.id = jobId

      jobAdView.site_name = site

      for {
        categoryList <- getCategoryWithJobAdsNumberList(site)
      }yield Ok(views.html.editjob(site, jobAdView, categoryList))

  }

  def editJobAd(site: String, siteId: Int, id: Int) = Action.async(parse.multipartFormData(fileService.handleFilePartAsFile)) {
    request =>
      val param = request.body.asFormUrlEncoded
      var jobAdView = getCommonDataFromView(Some(param), caseName = "jobAd", param.size, fileCheck = true)

      jobAdView.id = id
      jobAdView.site_id = siteId
      jobAdView.logo = param.get("logo").head match {
        case logo: Vector[String] => Some(logo(0))
        case _ => None
      }

      val joblogoType = param.get("joblogo").head.head

      var result = for {
        editedJobAdId <- jobAdService.editJobAd(jobAdView)
        uploadFileId <- uploadFile(request, editedJobAdId, joblogoType)
      }yield (0)

      result.map {
        res =>
          Redirect(routes.SiteController.getAllSites())
      }

  }

  def getCommonDataFromView(param: Option[Map[String, Seq[String]]], caseName: String, paramSize: Int, fileCheck: Boolean): JobAdView = {
    var jobAdView = new JobAdView()

    jobAdView.title = param.get("title").head
    jobAdView.company_id = param.get("company_id").head.toInt
    jobAdView.category_id = param.get("category_id").head match {
      case id: String => Try(id.toInt) toOption
      case _ => None
    }

    if(fileCheck){
      if(param.get("joblogo").head =="link")
        jobAdView.externallink = Some(param.get("externallink").head)
    }else{
      jobAdView.externallink = Some(param.get("externallink").head)
    }


    caseName match {

      case "jobAd" => {
        val jobtype = param.get("jobtype").head

        if(jobtype =="basis_plus"){
          jobAdView.premium = Option(true)
        }else if(jobtype =="recommended"){
          jobAdView.allow_personalized = true
        }

        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        jobAdView.startdate = formatter.parseDateTime(param.get("startdate")(0).filterNot((x: Char) => x.isWhitespace)).getMillis
        jobAdView.enddate = formatter.parseDateTime(param.get("enddate")(0).filterNot((x: Char) => x.isWhitespace)).getMillis

      }

      case "editIndex" => {

        jobAdView.premium = param.get("premium").head match {
          case s: String => Try(s.toBoolean) toOption
          case _ => None
        }
        jobAdView.externallink = Some(param.get("externallink").head)

        jobAdView.allow_personalized = param.get("allow_personalized").head.toBoolean

        jobAdView.logo = param.get("logo").head match {
          case logo: String => Try(logo.toString) toOption
          case _ => None
        }
        jobAdView.company_name = param.get("company_name").head

        jobAdView.startdate = param.get("startdate").head.filterNot((x: Char) => x.isWhitespace).toLong
        jobAdView.enddate = param.get("enddate").head.filterNot((x: Char) => x.isWhitespace).toLong

        jobAdView.site_id = param.get("site_id").head.toInt
      }

    }

    return jobAdView
  }

}


