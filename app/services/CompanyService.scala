package services

import models.{Company, CompanyView}
import play.api.Configuration

import scala.concurrent.Future

trait CompanyService {

  def getAllCompanies() : Future[List[Company]]

  def getAllCompaniesWithSpecialAgreement() : Future[List[CompanyView]]
}
