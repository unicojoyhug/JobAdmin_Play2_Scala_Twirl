package services

import models.{Category, Company, JobAd, JobAdView}
import play.api.Configuration

import scala.concurrent.Future

trait JobAdService {
  def getMsg(): String

  def getAllJobAdViews (site: String): Future[List[JobAdView]]

  //def createJobAd(jobAdView: JobAdView, site: String) : Future[Int]

}
