package services

import models.SpecialAgreement

import scala.concurrent.Future

trait SpecialAgreementService {

  def getAllSpecialAgreements(): Future[List[SpecialAgreement]]

}
