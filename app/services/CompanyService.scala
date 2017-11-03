package services

import models.Company
import play.api.Configuration

import scala.concurrent.Future

trait CompanyService {

  def getAllCompanies() : Future[List[Company]]
}
