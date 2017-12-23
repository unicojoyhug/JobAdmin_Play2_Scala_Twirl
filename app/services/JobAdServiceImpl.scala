package services

import javax.inject.Inject

import models._
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class JobAdServiceImpl @Inject()(configuration: Configuration, jobApiService: JobApiService, companyService: CompanyService, categoryService: CategoryService, siteService: SiteService ) extends JobAdService {

  val admin = configuration.get[String]("admin")

  override def getMsg() = "Vælg Site"

  def getAllJobs(site: String): Future[List[JobAd]] = {

    implicit val format = Json.reads[JobAd]
    val futureResponse: Future[List[JobAd]] = jobApiService.getAllJobAdViews(site).map {
      result => result.json.as[List[JobAd]]
    }

    return futureResponse
  }


  override def getAllJobAdViews(site: String): Future[List[JobAdView]] = {

    for {
      jobAdList <- getAllJobs(site)
      companyList <- companyService.getAllCompanies()
      categoryList <- categoryService.getAllCategoriesBySite(site)
      siteList <- siteService.getAllSites()
    } yield convertDomainToViewModel(jobAdList, companyList, categoryList, siteList)

  }


  override def createJobAd(jobAdView: JobAdView): Future[Int] = {

    var jobAd = convertJobAdViewToJsValue(jobAdView)

    val futureResponse = jobApiService.createJobAd(jobAd).map {
      result => (result.json \ "job_id").as[Int]
    }

    return futureResponse
  }

  override def deleteJobAd(jobAdId: Int): Future[String] = {
    val futureResponse = jobApiService.deleteJobAd(jobAdId).map {
      result => (result.json \ "Status").as[String]
    }
    return futureResponse
  }

  override def editJobAd(jobAdView: JobAdView): Future[Int] = {
    var jobAd = convertJobAdViewToJsValue(jobAdView)

    val futureResponse = jobApiService.editJobAd(jobAd).map {
      result => (result.json \ "job_id").as[Int]
    }

    return futureResponse
  }

  override def getUnexpiredJobAdList(list: List[JobAdView]): Future[List[JobAdView]] = {
    var yesterday = DateTime.now().minusDays(1).getMillis

    Future.successful(list.filter(_.enddate>yesterday))
  }

  override def getExpiredJobAdList(list: List[JobAdView]): Future[List[JobAdView]] = {
    var today = DateTime.now().getMillis
    Future.successful(list.filter(_.enddate<today))
  }

  def convertJobAdViewToJsValue(jobAdView: JobAdView): JsValue = {
    implicit val format = Json.writes[JobAd]

    var jobAd = new JobAd(
      Option(jobAdView.id),
      jobAdView.title,
      jobAdView.logo,
      admin,
      jobAdView.premium,
      jobAdView.externallink.getOrElse(""),
      jobAdView.startdate,
      jobAdView.enddate,
      DateTime.now().getMillis,
      DateTime.now().getMillis,
      jobAdView.category_id,
      jobAdView.site_id,
      jobAdView.company_id,
      jobAdView.allow_personalized

    )
    return Json.toJson[JobAd](jobAd)
  }


  def convertDomainToViewModel(jobAdsResult: List[JobAd], companiesResult: List[Company], categoriesResult: List[Category], siteResult: List[Site]): List[JobAdView] = {
    var jobAdViewList = ListBuffer[JobAdView]()

    for (jobAd <- jobAdsResult) {
      val jobAdView = new JobAdView()
      jobAdView.id = jobAd.id.getOrElse(-1)
      jobAdView.title = jobAd.title
      jobAdView.logo = jobAd.logo
      jobAdView.category_id = Some(jobAd.category_id.getOrElse(-1))

      jobAdView.category_id match {
        case Some(-1) => jobAdView.category_name = Option("N/A")
        case Some(id: Int) => jobAdView.category_name = Option(categoriesResult.find(c => c.id == id).get.name)
        case _ => None
      }

      jobAdView.company_id = jobAd.company_id

      jobAdView.company_name = companiesResult.find(c => c.id.getOrElse(-1) == jobAd.company_id).get.name

      jobAdView.premium = jobAd.premium
      jobAdView.allow_personalized = jobAd.allow_personalized

      jobAdView.externallink = Option(jobAd.externallink)
      jobAdView.site_id = jobAd.site_id
      jobAdView.site_name = siteResult.find(s => s.id == jobAd.site_id).get.name

      jobAdView.startdate = jobAd.startdate
      jobAdView.enddate = jobAd.enddate

      jobAdViewList += jobAdView
    }

    return jobAdViewList.toList

  }
}
