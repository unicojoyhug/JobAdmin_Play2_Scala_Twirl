package services

import javax.inject.Inject

import models.Company

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompanyServiceImpl  @Inject()(ws: WSClient, configuration: Configuration) extends CompanyService {
  override def getAllCompanies(): Future[List[Company]] = {

    val url: String = configuration.get[String]("job_api.url")
    val api_key: String = configuration.get[String]("security.apikeys")

    implicit val format = Json.reads[Company]

    val futureResponse: Future[List[Company]] = ws.url(s"$url/companies").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[Company]]
    }

    return futureResponse
  }
}
