package services

import javax.inject.Inject

import models.{Company, CompanyView, SpecialAgreement}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompanyServiceImpl  @Inject()(ws: WSClient, configuration: Configuration, specialAgreementService: SpecialAgreementService) extends CompanyService {
  val url: String = configuration.get[String]("job_api.url")
  val api_key: String = configuration.get[String]("security.apikeys")
  val admin: String = configuration.get[String]("admin")

  override def getAllCompanies(): Future[List[Company]] = {

    implicit val format = Json.reads[Company]

    val futureResponse: Future[List[Company]] = ws.url(s"$url/companies").addHttpHeaders("X-API-KEY" -> api_key).get().map {
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

      if(specialAgreementResult.exists(c => c.company_id == company.id.getOrElse(-1))){
        companyView.specialAgreement = true
      }

      companyViewList += companyView

    }

    return companyViewList.toList
  }
}
