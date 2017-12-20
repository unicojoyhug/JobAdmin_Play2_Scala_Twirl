package services

import javax.inject._

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import models.Site

@Singleton
class SiteServiceImpl @Inject()(jobApiService: JobApiService) extends SiteService {


  override def getAllSites(): Future[List[Site]] = {
    implicit val format = Json.reads[Site]
    val futureResponse: Future[List[Site]] = jobApiService.getAllSites().map {
      result => result.json.as[List[Site]]
    }

    return futureResponse
  }

  override def getMsg() = "Vælg Site"

}
