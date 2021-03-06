package controllers

import java.io.File
import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents, MultipartFormData, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

import models.CompanyView
import services.{CompanyService, FileService, SpecialAgreementService}

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
    company.specialAgreementId = Some(param.get("specialAgreementId").head.toInt)
    company.logo = param.get("logo").head

    Ok(views.html.editcompany(company))
  }

  def editCompany(companyId: Int) = Action.async(parse.multipartFormData(fileService.handleFilePartAsFile)) { implicit request =>
    val param = request.body.asFormUrlEncoded

    var company = new CompanyView()
    company.id = companyId
    company.name = param.get("name").head.head
    company.specialAgreementId = Some(param.get("specialAgreementId").head.head.toInt)
    company.specialAgreement = param.get("specialAgreement").head.head.toBoolean
    company.logo = param.get("externallink").head.head


    val companyLogo = param.get("companylogo").head.head

    val result = for {
      editedCompanyId <- companyService.editCompany(company)
      _ <- uploadFile(request, editedCompanyId, companyLogo)
      specialAgreementId <- setSpecialAgreement(editedCompanyId, company.specialAgreement, company.specialAgreementId)

    }yield specialAgreementId

    result.map{ res =>
      Redirect(routes.CompanyController.getAllCompanyViews())
    }
  }

  private def setSpecialAgreement(editedCompanyId: Int, hasSpecialAgreement: Boolean, specialAgreementId: Option[Int]): Future[Int] = {
    hasSpecialAgreement match {
      case true => specialAgreementService.createSpecialAgreements(editedCompanyId)
      case false => deleteSpecialAgreement(specialAgreementId)
    }
  }

  private def deleteSpecialAgreement(specialAgreementId: Option[Int]): Future[Int] ={
    specialAgreementId match {
      case Some(id:Int) => specialAgreementService.deleteSpecialAgreements(id)
      case _ =>  Future.successful(0)
    }
  }

  private def uploadFile(request: Request[MultipartFormData[File]], companyId: Int, companyLogo: String): Future[Try[Int]] = {

    if (companyLogo == "image") {
      fileService.uploadFile(request.body.file("image"), companyId, caseName = "companies")
    } else {
       Future.successful(Failure(new Exception("No file to upload")))
    }
  }

  def index() = Action{
    Ok(views.html.createcompany())
  }

  def createCompany() = Action.async(parse.multipartFormData(fileService.handleFilePartAsFile)) {  request =>

    val param = request.body.asFormUrlEncoded

    var company = new CompanyView()
    company.name = param.get("name").head.head
    company.specialAgreement = param.get("specialAgreement").head.head.toBoolean

    val result = for {
      newCompanyId <- companyService.createCompany(company)
      _ <- uploadFile(request, newCompanyId, "image")
      specialAgreementId <- setSpecialAgreement(newCompanyId, company.specialAgreement, None)

    }yield specialAgreementId

    result.map{ res =>
      Redirect(routes.CompanyController.getAllCompanyViews())
    }
  }
}
