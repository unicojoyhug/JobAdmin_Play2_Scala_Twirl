package services

import javax.inject.Inject

import models.{Category, CategoryWithNumberOfJobsView, JobAdView}

import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CategoryServiceImpl @Inject()(jobApiService: JobApiService, site: String) extends CategoryService {

  override def getAllCategoriesBySite(site: String): Future[List[Category]] = {

    implicit val format = Json.reads[Category]

    val futureResponse: Future[List[Category]] = jobApiService.getAllCategoriesBySite(site).map {
      result => result.json.as[List[Category]]
    }

    return futureResponse
  }

  override def getCategoryWithNumberOfJobAdsBySite(joblist: List[JobAdView], site: String, categoryList: List[Category]): List[CategoryWithNumberOfJobsView] = {

    var result = ListBuffer[CategoryWithNumberOfJobsView]()

    for(item <- categoryList){
      var jobNumber = 0
      jobNumber = joblist.count(job => job.category_id.getOrElse(-1) == item.id)
      val categoryWithJobNumber = CategoryWithNumberOfJobsView(
        site,
        item.id,
        item.name,
        jobNumber
      )
      result += categoryWithJobNumber
    }

    return result.toList
  }
}
