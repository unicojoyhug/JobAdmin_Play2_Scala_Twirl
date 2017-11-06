package services

import javax.inject.Inject

import models.{Category, Company, JobAd, JobAdView}
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.libs.ws.JsonBodyWritables._

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class JobAdServiceImpl @Inject()(ws: WSClient, companyService: CompanyService, categoryService: CategoryService, configuration: Configuration ) extends JobAdService {
  val api_key: String = configuration.get[String]("security.apikeys")
  val url: String = configuration.get[String]("job_api.url")
  val admin: String = configuration.get[String]("admin")


  def getAllJobs(site: String): Future[List[JobAd]] = {

    implicit val format = Json.reads[JobAd]
    val futureResponse: Future[List[JobAd]] = ws.url(s"$url/$site/jobs").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[JobAd]]
    }


    return futureResponse
  }

  override def getMsg() = "Vælg Site"

  override def getAllJobAdViews (site: String): Future[List[JobAdView]] = {

    for {
      r1 <- getAllJobs(site)
      r2 <- companyService.getAllCompanies()
      r3 <- categoryService.getAllCategoriesBySite(site)
    } yield convertDomainToViewModel(r1, r2, r3)

  }


  override def createJobAd(jobAdView: JobAdView): Future[Int] = {

    var jobAd = convertJobAdViewToJsValue(jobAdView)

    val futureResponse = ws.url(s"$url/jobs").addHttpHeaders("X-API-KEY" -> api_key).post(jobAd).map {
      result => (result.json \ "job_id").as[Int]
    }

    return futureResponse
  }


  def convertJobAdViewToJsValue(jobAdView: JobAdView): JsValue = {
    implicit val format = Json.writes[JobAd]

    var jobAd = new JobAd(
      None,
      jobAdView.title,
      jobAdView.logo,
      admin,
      jobAdView.premium,
      jobAdView.externallink,
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



  def convertDomainToViewModel(jobAdsResult: List[JobAd], companiesResult: List[Company], categoriesResult: List[Category]): List[JobAdView] = {
    var jobAdViewList = ListBuffer[JobAdView]()

    for (jobAd <- jobAdsResult){
      val jobAdView = new JobAdView()

      jobAdView.id = jobAd.id
      jobAdView.title = jobAd.title
      jobAdView.logo = jobAd.logo
      jobAdView.category_id = Some(jobAd.category_id.getOrElse(-1))

      jobAdView.category_id match {
        case Some(-1) => jobAdView.category_name = Option("N/A")
        case Some(id: Int)=>jobAdView.category_name = Option(categoriesResult.find(c=> c.id == id).get.name)
        case _ => None
      }

      jobAdView.company_id = jobAd.company_id
      jobAdView.company_name = companiesResult.find(c => c.id == jobAd.company_id).get.name

      //TODO: Need to check if they are needed
      jobAdView.premium = jobAd.premium
      jobAdView.allow_personalized = jobAd.allow_personalized

      jobAdView.externallink = jobAd.externallink
      jobAdView.site_id = jobAd.site_id

      jobAdView.startdate = jobAd.startdate
      jobAdView.enddate = jobAd.enddate

      jobAdViewList += jobAdView
    }

    return jobAdViewList.toList

  }

}