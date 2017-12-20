package services

import java.io.File
import javax.inject.Inject

import akka.stream.scaladsl.{FileIO, Source}
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.Future

class JobApiServiceImpl @Inject()(ws: WSClient, configuration: Configuration) extends JobApiService {
  val url: String = configuration.get[String]("job_api.url")
  val api_key: String = configuration.get[String]("security.apikeys")
  val admin: String = configuration.get[String]("admin")

  //Company
  override def getAllCompanies() = ws.url(s"$url/companies").addHttpHeaders("X-API-KEY" -> api_key).get()

  override def editCompany(company: JsValue) = ws.url(s"$url/companies").addHttpHeaders("X-API-KEY" -> api_key).put(company)

  override def createCompany(company: JsValue) = ws.url(s"$url/companies").addHttpHeaders("X-API-KEY" -> api_key).post(company)

  override def getAllJobAdViews(site: String) = ws.url(s"$url/$site/jobs").addHttpHeaders("X-API-KEY" -> api_key).get()


  //JobAd
  override def createJobAd(jobAd: JsValue) = ws.url(s"$url/jobs").addHttpHeaders("X-API-KEY" -> api_key).post(jobAd)

  override def deleteJobAd(jobAdId: Int) = ws.url(s"$url/jobs/$jobAdId").addHttpHeaders("X-API-KEY" -> api_key).delete()

  override def editJobAd(jobAd: JsValue) = ws.url(s"$url/jobs").addHttpHeaders("X-API-KEY" -> api_key).put(jobAd)


  //Site
  override def getAllSites(): Future[WSResponse] = ws.url(s"$url/sites").addHttpHeaders("X-API-KEY" -> api_key).get()


  //File Uploading
  override def uploadFile(id: Int, caseName: String, key: String, filename: String, contentType: Option[String], file: File) =
    ws.url(s"$url/$caseName/$key/$id").addHttpHeaders("X-API-KEY" -> api_key)
      .post(Source(FilePart(key, s"${filename}", Option(s"${contentType}"), FileIO.fromPath(file.toPath)) :: DataPart("key", "value") :: List()))


  //Special Agreement
  override def getAllSpecialAgreements() = ws.url(s"$url/specialagreements").addHttpHeaders("X-API-KEY" -> api_key).get()

  override def createSpecialAgreements(specialAgreementJson: JsValue) = ws.url(s"$url/specialagreements").addHttpHeaders("X-API-KEY" -> api_key).post(specialAgreementJson)

  override def deleteSpecialAgreements(id: Int) = ws.url(s"$url/specialagreements/$id").addHttpHeaders("X-API-KEY" -> api_key).delete()


  //Category
  override def getAllCategoriesBySite(site: String) = ws.url(s"$url/$site/categories").addHttpHeaders("X-API-KEY" -> api_key).get()
}
