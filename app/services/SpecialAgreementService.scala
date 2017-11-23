package services

import models.SpecialAgreement

import scala.concurrent.Future

trait SpecialAgreementService {

  def getAllSpecialAgreements(): Future[List[SpecialAgreement]]

  def createSpecialAgreements(companyId: Int) : Future[Int]

}
