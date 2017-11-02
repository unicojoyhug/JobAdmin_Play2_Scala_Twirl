package services

import javax.inject.Inject

import models.{Category, Company, JobAd, JobAdView}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class JobAdServiceImpl @Inject()(ws: WSClient) extends JobAdService {

  override def getAllJobs(configuration: Configuration, site: String): Future[List[JobAd]] = {
    val url: String = configuration.get[String]("job_api.url")
    val api_key: String = configuration.get[String]("security.apikeys")

    implicit val format = Json.reads[JobAd]
    val futureResponse: Future[List[JobAd]] = ws.url(s"$url/$site/jobs").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[JobAd]]
    }


    return futureResponse
  }

  override def getMsg() = "Vælg Site"

  override def convertDomainToViewModel(jobAdsResult: List[JobAd], companiesResult: List[Company], categoriesResult: List[Category]): List[JobAdView] = {
    var jobAdViewList = ListBuffer[JobAdView]()

    for (jobAd <- jobAdsResult){
      val jobAdView = new JobAdView()

      jobAdView.id = jobAd.id
      jobAdView.title = jobAd.title
      jobAdView.logo = jobAd.logo
      jobAdView.category_id = jobAd.category_id.getOrElse(-1)

      if(jobAdView.category_id != -1){
        jobAdView.category_name =
          categoriesResult.find(c => c.id == jobAdView.category_id).get.name
      }else{
        jobAdView.category_name = "N/A"
      }

      jobAdView.company_id = jobAd.company_id
      jobAdView.company_name = companiesResult.find(c => c.id == jobAd.company_id).get.name

      //TODO: Need to check if they are needed
      jobAdView.premium = jobAd.premium
      jobAdView.allow_personalized = jobAd.allow_personalized

      jobAdView.createdby = jobAd.createdby
      jobAdView.externallink = jobAd.externallink
      jobAdView.trackinglink = jobAd.trackinglink
      jobAdView.site_id = jobAd.site_id

      jobAdView.startdate = jobAd.startdate
      jobAdView.enddate = jobAd.enddate
      jobAdView.site_id = jobAd.site_id

      jobAdViewList += jobAdView
    }

    return jobAdViewList.toList

  }
}