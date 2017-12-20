package services

import java.io.File

import models.SpecialAgreement
import play.api.libs.json.JsValue
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

trait JobApiService {
  def getAllCompanies(): Future[WSResponse]

  def editCompany(company: JsValue) : Future[WSResponse]

  def createCompany(company: JsValue) : Future[WSResponse]

  def getAllJobAdViews (site: String): Future[WSResponse]

  def createJobAd(jobAd: JsValue) : Future[WSResponse]

  def deleteJobAd(jobAdId: Int) : Future[WSResponse]

  def editJobAd(jobAd: JsValue) : Future[WSResponse]

  def getAllSites() : Future[WSResponse]

  def uploadFile(id: Int, caseName: String, key: String, filename: String, contentType: Option[String], file: File) : Future[WSResponse]

  def getAllSpecialAgreements(): Future[WSResponse]

  def createSpecialAgreements(specialAgreement: JsValue) : Future[WSResponse]

  def deleteSpecialAgreements(id: Int) : Future[WSResponse]

  def getAllCategoriesBySite(site: String) : Future[WSResponse]
}
