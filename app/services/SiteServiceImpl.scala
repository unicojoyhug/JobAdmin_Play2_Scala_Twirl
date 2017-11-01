package services

import javax.inject._

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import models.Site

@Singleton
class SiteServiceImpl @Inject()(ws: WSClient) extends SiteService {


  override def getAllSites(configuration: Configuration): Future[List[Site]] = {
    val url:String = configuration.get[String]("job_api.url")
    val api_key: String = configuration.get[String]("security.apikeys")
    implicit val format = Json.reads[Site]
    val futureResponse: Future[List[Site]] = ws.url(s"$url/sites").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[Site]]
    }

    return futureResponse
  }

  override def getMsg() = "Vælg Site"

}
