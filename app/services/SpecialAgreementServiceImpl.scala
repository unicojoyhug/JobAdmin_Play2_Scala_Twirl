package services

import javax.inject.Inject

import models.SpecialAgreement
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SpecialAgreementServiceImpl @Inject()(ws: WSClient, configuration: Configuration) extends SpecialAgreementService {
  val url:String = configuration.get[String]("job_api.url")
  val api_key: String = configuration.get[String]("security.apikeys")
  val admin: String = configuration.get[String]("admin")

  override def getAllSpecialAgreements() : Future[List[SpecialAgreement]] = {

    implicit val format = Json.reads[SpecialAgreement]
    val futureResponse: Future[List[SpecialAgreement]] = ws.url(s"$url/specialagreements").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[SpecialAgreement]]
    }

    return futureResponse
  }
}
