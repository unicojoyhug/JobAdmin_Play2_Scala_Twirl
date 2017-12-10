package services

import models.{Category, CategoryWithNumberOfJobsView, JobAdView}
import play.api.Configuration

import scala.concurrent.Future

trait CategoryService {

  def getAllCategoriesBySite(site:String) : Future[List[Category]]
  def getCategoryWithNumberOfJobAdsBySite(joblist: List[JobAdView], site: String, categoryList: List[Category]) : List[CategoryWithNumberOfJobsView]
}
