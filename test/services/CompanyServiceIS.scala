
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play._
import play.api.mvc._
import services.{CompanyService, SiteService}

import scala.concurrent.Await
import scala.concurrent.duration._


class CompanyServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder{
  val sut = injector.instanceOf[CompanyService]

  "#getAllCompanies" should {
    "return companies" in {
      val lists = sut.getAllCompanies()
      val companyLength = Await.result(lists, Duration(10, SECONDS)).length
      withClue("Company length should be: "){
        companyLength mustBe 386
      }
    }

    "#getAllCompanyView" should {
      "return view" in {
        val lists = sut.getAllCompaniesWithSpecialAgreement()
        val companyResult = Await.result(lists, Duration(10, SECONDS))
        withClue("View length should be: "){

          companyResult.exists(c => c.specialAgreement) mustBe false
        }
      }
    }
  }
}
