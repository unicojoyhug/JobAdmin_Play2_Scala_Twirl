package controllers

import javax.inject.Inject

import models.CompanyView
import play.api.mvc.{AbstractController, ControllerComponents}
import services.CompanyService
import scala.concurrent.ExecutionContext.Implicits.global


class CompanyController @Inject()(cc: ControllerComponents, companyService: CompanyService)
  extends AbstractController(cc) with play.api.i18n.I18nSupport{

  def getAllCompanyViews() = Action.async {
    val lists : scala.concurrent.Future[List[CompanyView]] = companyService.getAllCompaniesWithSpecialAgreement()

    lists map {
      list =>  Ok(views.html.companys( list))
    }
  }
  
}
