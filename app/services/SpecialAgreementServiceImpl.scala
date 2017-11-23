package services

import javax.inject.Inject

import models.SpecialAgreement
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SpecialAgreementServiceImpl @Inject()(ws: WSClient, configuration: Configuration) extends SpecialAgreementService {
  val url:String = configuration.get[String]("job_api.url")
  val api_key: String = configuration.get[String]("security.apikeys")
  val admin: String = configuration.get[String]("admin")

  override def getAllSpecialAgreements(): Future[List[SpecialAgreement]] = {

    implicit val format = Json.reads[SpecialAgreement]
    val futureResponse: Future[List[SpecialAgreement]] = ws.url(s"$url/specialagreements").addHttpHeaders("X-API-KEY" -> api_key).get().map {
      result => result.json.as[List[SpecialAgreement]]
    }

    return futureResponse
  }

  override def createSpecialAgreements(companyId: Int): Future[Int] = {
    implicit val format = Json.writes[SpecialAgreement]

    val specialAgreement = new SpecialAgreement(
      None,
      "maengderabat",
      DateTime.now().getMillis,
      DateTime.now().getMillis,
      companyId
    )

    val specialAgreementJson =  Json.toJson[SpecialAgreement](specialAgreement)

    val futureResponse = ws.url(s"$url/specialagreements").addHttpHeaders("X-API-KEY" -> api_key).post(specialAgreementJson).map {
      result => (result.json \ "specialagreement_id").as[Int]

    }

    return futureResponse
  }

  override def deleteSpecialAgreements(id: Int): Future[Int] = {
    //v1/admin/specialagreements/:id

    val futureResponse = ws.url(s"$url/specialagreements/$id").addHttpHeaders("X-API-KEY" -> api_key).delete().map {
      result => (result.json \ "Status").as[String] match {
        case "Ok" => 1
        case _ => -1
      }
    }
    return futureResponse

  }
}
