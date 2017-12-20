package services

import javax.inject.Inject

import models.{Company, CompanyView, SpecialAgreement}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompanyServiceImpl  @Inject()(jobApiService: JobApiService, specialAgreementService: SpecialAgreementService) extends CompanyService {

  override def getAllCompanies(): Future[List[Company]] = {

    implicit val format = Json.reads[Company]

    val futureResponse: Future[List[Company]] = jobApiService.getAllCompanies().map {
      result => result.json.as[List[Company]]
    }

    return futureResponse
  }

  override def getAllCompaniesWithSpecialAgreement(): Future[List[CompanyView]] = {


    for {
      r1 <- getAllCompanies()
      r2 <- specialAgreementService.getAllSpecialAgreements()

    }yield convertCompanyDomainToView(r1, r2)

  }

  def convertCompanyDomainToView (companyResult: List[Company], specialAgreementResult: List[SpecialAgreement]): List[CompanyView] = {
    var companyViewList = ListBuffer[CompanyView]()

    for(company <- companyResult) {
      var companyView = new CompanyView()
      companyView.id = company.id.getOrElse(-1)
      companyView.name = company.name
      companyView.logo = company.logo

      companyView.specialAgreementId = specialAgreementResult.find(c => c.company_id == companyView.id) match {
        case Some(x) =>
          companyView.specialAgreement = true
          x.id
        case _ =>
          companyView.specialAgreement = false
          Option(-1)
      }

      companyViewList += companyView
    }

    return companyViewList.toList
  }

  override def editCompany(companyView: CompanyView): Future[Int] = {
    var company = convertCompanyViewToJsValue(companyView)

    val futureResponse = jobApiService.editCompany(company).map {
      result => (result.json \ "company_id").as[Int]
    }

    return futureResponse
  }

  def convertCompanyViewToJsValue(companyView: CompanyView): JsValue = {
    implicit val format = Json.writes[Company]

    var company = new Company(
      Option(companyView.id),
      companyView.name,
      DateTime.now().getMillis,
      DateTime.now().getMillis,
      companyView.logo
    )

    return  Json.toJson[Company](company)
  }

  override def createCompany(companyView: CompanyView): Future[Int]  = {

    var company = convertCompanyViewToJsValue(companyView)

    val futureResponse = jobApiService.createCompany(company).map {

        result => result match {
          case x if 200 until 299 contains x.status.intValue => (result.json \ "company_id").as[Int]

          case resultCode =>
            println("#########################")
            println(s"Failed to  : " + (result.json \ "Status").as[String])
            println("#########################")

            -1
        }
    }

    return futureResponse
  }
}
