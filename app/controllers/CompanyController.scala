package controllers

import javax.inject.Inject

import models.CompanyView
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{CompanyService, FileService, SpecialAgreementService}

import scala.concurrent.ExecutionContext.Implicits.global


class CompanyController @Inject()(cc: ControllerComponents, companyService: CompanyService, fileService:FileService, specialAgreementService: SpecialAgreementService)
  extends AbstractController(cc) with play.api.i18n.I18nSupport{

  def getAllCompanyViews() = Action.async {
    val lists : scala.concurrent.Future[List[CompanyView]] = companyService.getAllCompaniesWithSpecialAgreement()

    lists map {
      list =>  Ok(views.html.companies(list))
    }
  }

  def editIndex(companyId: Int) = Action { implicit request =>
    val param = request.body.asFormUrlEncoded
    var company = new CompanyView()
    company.id = companyId
    company.name = param.get("name").head
    company.specialAgreement = param.get("specialAgreement").head.toBoolean
    company.logo = param.get("logo").head

    Ok(views.html.editcompany(company))
  }

  def editCompany(companyId: Int) = Action.async(parse.multipartFormData(fileService.handleFilePartAsFile)) { implicit request =>
    val param = request.body.asFormUrlEncoded

    var company = new CompanyView()
    company.id = companyId
    company.name = param.get("name").head.head
    company.specialAgreement = param.get("specialAgreement").head.head.toBoolean
    company.logo = param.get("externallink").head.head

    val editedCompanyId: scala.concurrent.Future[Int] = companyService.editCompany(company)

    editedCompanyId map {
      id =>
        if (param.get("companylogo").head.head == "image") {
          fileService.uploadFile(request.body.file("image"), id, caseName = "companies")
        }

        if (company.specialAgreement) {
          val specialAgreementId = specialAgreementService.createSpecialAgreements(id).map {
            result =>
              Logger.debug("New SpecialAgreement id = " + result)
          }
        }

        Redirect(routes.CompanyController.getAllCompanyViews())
    }
  }

  def index() = Action{
    Ok(views.html.createcompany())
  }

  def createCompany() = Action.async(parse.multipartFormData(fileService.handleFilePartAsFile)) { implicit request =>

    val param = request.body.asFormUrlEncoded

    var company = new CompanyView()
    company.name = param.get("name").head.head
    company.specialAgreement = param.get("specialAgreement").head.head.toBoolean

    val newCompanyId : scala.concurrent.Future[Int] = companyService.createCompany(company)

    newCompanyId map {
      id =>
        Logger.debug("New Company id = " + id)

        if(id != -1){
          //TODO: need to make Future return to get right redirect
          fileService.uploadFile(request.body.file("image"), id, caseName = "companies")
        }

        if(company.specialAgreement){
          val specialAgreementId = specialAgreementService.createSpecialAgreements(id).map{
            result =>
              Logger.debug("New SpecialAgreement id = " + result)
          }
        }
        Redirect(routes.CompanyController.getAllCompanyViews())
    }

  }
}
