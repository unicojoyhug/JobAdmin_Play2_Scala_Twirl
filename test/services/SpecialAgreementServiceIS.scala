
import models.{CompanyView, SpecialAgreement}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play._
import play.api.mvc._
import services.SpecialAgreementService

import scala.concurrent.Await
import scala.concurrent.duration._


class SpecialAgreementServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder{
  val sut = injector.instanceOf[SpecialAgreementService]

    "#createCompanyWithSpecialAgreement" should {
      "return SpecialAgreementId" in {

        val result = sut.createSpecialAgreements(398)
        val companyResult = Await.result(result, Duration(10, SECONDS))

        withClue("Returned id should be: "){

          companyResult mustBe 1
        }
      }
    }
}

