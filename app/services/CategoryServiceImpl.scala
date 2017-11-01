package services

import javax.inject.Inject

import models.{Category}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CategoryServiceImpl @Inject()(ws: WSClient, site: String) extends CategoryService {

  override def getAllCategoriesBySite(configuration: Configuration, site: String): Future[List[Category]] = {

    val url: String = configuration.get[String]("job_api.url")
    val api_key: String = configuration.get[String]("security.apikeys")

    implicit val format = Json.reads[Category]

    val futureResponse: Future[List[Category]] = ws.url(s"$url/$site/categories").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[Category]]
    }

    return futureResponse
  }
}
