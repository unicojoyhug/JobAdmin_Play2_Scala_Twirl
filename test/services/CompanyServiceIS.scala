
import models.CompanyView
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play._
import play.api.mvc._
import services.{CompanyService, SiteService}

import scala.concurrent.Await
import scala.concurrent.duration._


class CompanyServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder {
  val sut = injector.instanceOf[CompanyService]

  "#getAllCompanies" should {
    "return companies" in {
      val lists = sut.getAllCompanies()
      val companyLength = Await.result(lists, Duration(10, SECONDS)).length
      withClue("Company length should be: ") {
        companyLength mustBe 398
      }
    }
  }

    "#getAllCompanyView" should {
      "return view" in {
        val lists = sut.getAllCompaniesWithSpecialAgreement()
        val companyResult = Await.result(lists, Duration(10, SECONDS))
        withClue("View length should be: ") {

          companyResult.exists(c => c.specialAgreement) mustBe false
        }
      }
    }

    "#createCompany" should {
      "return companyId" in {

        val companyView = new CompanyView()
        companyView.name = "Test Company"
        companyView.specialAgreement = true
        companyView.logo = ""
        val result = sut.createCompany(companyView)
        val companyResult = Await.result(result, Duration(10, SECONDS))

        withClue("New id should be: ") {

          companyResult mustBe 399
        }
      }
    }

    "#createCompanyWithSpecialAgreement" should {
      "return companyId" in {

        val companyView = new CompanyView()
        companyView.name = "Test Company to edit"
        companyView.specialAgreement = true
        companyView.logo = ""
        val result = sut.createCompany(companyView)
        val companyResult = Await.result(result, Duration(10, SECONDS))

        withClue("New id should be: ") {

          companyResult mustBe 340
        }
      }
    }


    "#editCompany" should {
      "return companyId" in {

        val companyView = new CompanyView()
        companyView.id = 396
        companyView.name = "Test Company edited new"
        companyView.specialAgreement = true
        companyView.logo = ""
        val result = sut.editCompany(companyView)
        val companyResult = Await.result(result, Duration(10, SECONDS))

        withClue("Edited id should be: ") {

          companyResult mustBe 396
        }
      }
    }

}
