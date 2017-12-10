package services

import javax.inject.Inject

import models.{Category, CategoryWithNumberOfJobsView, JobAdView}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CategoryServiceImpl @Inject()(ws: WSClient, configuration: Configuration, site: String) extends CategoryService {

  override def getAllCategoriesBySite(site: String): Future[List[Category]] = {

    val url: String = configuration.get[String]("job_api.url")
    val api_key: String = configuration.get[String]("security.apikeys")

    implicit val format = Json.reads[Category]

    val futureResponse: Future[List[Category]] = ws.url(s"$url/$site/categories").addHttpHeaders("X-API-KEY" -> api_key).get().map {
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
