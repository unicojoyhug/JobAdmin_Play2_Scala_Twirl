package services

import models.{Category, Company, JobAd, JobAdView}
import play.api.Configuration

import scala.concurrent.Future

trait JobAdService {
  def getMsg(): String

  def getAllJobAdViews (site: String): Future[List[JobAdView]]

  def createJobAd(jobAdView: JobAdView) : Future[Int]

  def deleteJobAd(jobAdId: Int) : Future[String]

  def editJobAd(jobAdView: JobAdView) : Future[Int]

  //def getActiveJobAdViews (site:String): Future[List[JobAdView]]

  //def getExpiredJobAdViews (site: String): Future[List[JobAdView]]

 // def getJobAdById(jobAdId: Int) : Future[JobAdView]

}
