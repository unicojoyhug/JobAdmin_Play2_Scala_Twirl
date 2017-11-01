package services

import models.{Category, Company, JobAd, JobAdView}
import play.api.Configuration

import scala.concurrent.Future

trait JobAdService {
  def getAllJobs(configuration: Configuration, site: String) : Future[List[JobAd]]
  def getMsg(): String
  def convertDomainToViewModel(jobAdsResult: List[JobAd], companiesResult: List[Company], categoriesResult: List[Category]): List[JobAdView]
}
