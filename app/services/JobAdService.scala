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

  def getUnexpiredJobAdList(list: List[JobAdView]): Future[List[JobAdView]]

  def getExpiredJobAdList(list: List[JobAdView]): Future[List[JobAdView]]

}
