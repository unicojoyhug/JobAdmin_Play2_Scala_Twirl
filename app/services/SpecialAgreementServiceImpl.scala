package services

import javax.inject.Inject

import models.SpecialAgreement
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SpecialAgreementServiceImpl @Inject()(jobApiService: JobApiService) extends SpecialAgreementService {

  override def getAllSpecialAgreements(): Future[List[SpecialAgreement]] = {

    implicit val format = Json.reads[SpecialAgreement]
    val futureResponse: Future[List[SpecialAgreement]] = jobApiService.getAllSpecialAgreements().map {
      result => result.json.as[List[SpecialAgreement]]
    }

    return futureResponse
  }

  override def createSpecialAgreements(companyId: Int): Future[Int] = {
    implicit val format = Json.writes[SpecialAgreement]

    val specialAgreement = new SpecialAgreement(
      None,
      "maengderabat", //This value is temporary value set by JP.
      DateTime.now().getMillis,
      DateTime.now().getMillis,
      companyId
    )

    val specialAgreementJson =  Json.toJson[SpecialAgreement](specialAgreement)

    val futureResponse = jobApiService.createSpecialAgreements(specialAgreementJson).map {
      result => (result.json \ "specialagreement_id").as[Int]

    }

    return futureResponse
  }

  override def deleteSpecialAgreements(id: Int): Future[Int] = {

    val futureResponse = jobApiService.deleteSpecialAgreements(id).map {
      result => (result.json \ "Status").as[String] match {
        case "Ok" => 1
        case _ => -1
      }
    }
    return futureResponse

  }
}
