package controllers

import java.io.File
import java.nio.file.{Files, Path, Paths}
import javax.inject.{Inject, Singleton}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import models.JobAd
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.{DataPart, FilePart}
import play.core.parsers.Multipart.FileInfo

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import services.{CategoryService, CompanyService, FileService, JobAdService}
import models.{JobAdForm, JobAdView}
import play.api.libs.json.Writes
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import play.api.libs.ws.WSClient

@Singleton
class JobController @Inject()(cc: ControllerComponents, ws: WSClient, configuration: Configuration, fileService: FileService, jobAdService: JobAdService, categoryService: CategoryService, companyService: CompanyService)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def getAllJobAds (site: String) = Action.async {

    val lists : scala.concurrent.Future[List[JobAdView]] = jobAdService.getAllJobAdViews(site)

    lists map {
      list =>  Ok(views.html.jobs( jobAdService.getMsg(),list))
    }
  }


  def index(site:String, id: Int) = Action.async{
    implicit request =>
    for {
        r1 <-categoryService.getAllCategoriesBySite(site)
        r2 <-companyService.getAllCompanies()
     }yield Ok(views.html.createjob2(site, id, r2, r1))
  }


  type FilePartHandler[A] = FileInfo => Accumulator[ByteString, FilePart[A]]

  /**
    * Uses a custom FilePartHandler to return a type of "File" rather than
    * using Play's TemporaryFile class.  Deletion must happen explicitly on
    * completion, rather than TemporaryFile (which uses finalization to
    * delete temporary files).
    *
    * @return
    */
  private def handleFilePartAsFile: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType) =>
      val path: Path = Files.createTempFile("multipartBody", "tempFile")
      val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(path)
      val accumulator: Accumulator[ByteString, IOResult] = Accumulator(fileSink)
      accumulator.map {
        case IOResult(count, status) =>
          Logger.info(s"count = $count, status = $status")
          FilePart(partName, filename, contentType, path.toFile)
      }
  }



  def createJobAd(siteId: Int) = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>

    val param = request.body.asFormUrlEncoded

    var jobAdView = getCommonDataFromView(Some(param), caseName = "jobAd", param.size, fileCheck=true)

    jobAdView.site_id = siteId

    val key = "pdf"

    val newJobAdId: scala.concurrent.Future[Int] = jobAdService.createJobAd(jobAdView)

    newJobAdId map {
      id =>
        Logger.debug("New JobAd id = " + id)
        if (param.get("joblogo").head == Vector("pdf")) {
          fileService.uploadFile(request.body.file(key), id, caseName = "jobs")
        }
    }

    Redirect(routes.SiteController.getAllSites())
  }



  def deleteJobAd(site: String, id: Int) = Action.async {

    val job : scala.concurrent.Future[String] = jobAdService.deleteJobAd(id)

    job map {
      list =>
        Redirect(routes.JobController.getAllJobAds(site))
    }
  }

  def editIndex(site: String, jobId: Int) = Action.async{
    implicit request =>
      val param = request.body.asFormUrlEncoded
      var jobAdView = getCommonDataFromView(param, caseName = "editIndex", param.size, fileCheck=false)

      jobAdView.id = jobId

      jobAdView.site_name = site

      for {
        r1 <-categoryService.getAllCategoriesBySite(site)
      }yield Ok(views.html.editjob(jobAdView, r1))

  }

  def editJobAd(site: String, siteId: Int, id: Int) = Action(parse.multipartFormData(handleFilePartAsFile)) {
    implicit request =>
      val param = request.body.asFormUrlEncoded
      var jobAdView = getCommonDataFromView(Some(param), caseName = "jobAd", param.size, fileCheck = true)

      jobAdView.id = id
      jobAdView.site_id = siteId
      jobAdView.logo = param.get("logo").head match {
        case logo: Vector[String] => Some(logo(0))
        case _ => None
      }

      val key = "pdf"

      val editedJobAdId: scala.concurrent.Future[Int] = jobAdService.editJobAd(jobAdView)

      editedJobAdId map {
        id =>
          if (param.get("joblogo").head == Vector("pdf")) {
            fileService.uploadFile(request.body.file(key), id, caseName = "jobs")
          }
      }
      Redirect(routes.SiteController.getAllSites())
  }

  def getCommonDataFromView(param: Option[Map[String, Seq[String]]], caseName: String, paramSize: Int, fileCheck: Boolean): JobAdView = {
    var jobAdView = new JobAdView()

    jobAdView.title = param.get("title").head
    jobAdView.company_id = param.get("company_id").head.toInt
    jobAdView.category_id = param.get("category_id").head match {
      case id: String => Try(id.toInt) toOption
      case _ => None
    }



    if(fileCheck){
      if(param.get("joblogo").head =="link")
      jobAdView.externallink = Some(param.get("externallink").head)
    }else{
      jobAdView.externallink = Some(param.get("externallink").head)
    }


    caseName match {

      case "jobAd" => {
        val jobtype = param.get("jobtype").head

        if(jobtype =="basis_plus"){
          jobAdView.premium = Option(true)
        }else if(jobtype =="recommended"){
          jobAdView.allow_personalized = true
        }

        val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")
        jobAdView.startdate = formatter.parseDateTime(param.get("startdate")(0).filterNot((x: Char) => x.isWhitespace)).getMillis
        jobAdView.enddate = formatter.parseDateTime(param.get("enddate")(0).filterNot((x: Char) => x.isWhitespace)).getMillis

      }

      case "editIndex" => {

        jobAdView.premium = param.get("premium").head match {
          case s: String => Try(s.toBoolean) toOption
          case _ => None
        }

        jobAdView.allow_personalized = param.get("allow_personalized").head.toBoolean

        jobAdView.logo = param.get("logo").head match {
          case logo: String => Try(logo.toString) toOption
          case _ => None
        }
        jobAdView.company_name = param.get("company_name").head

        jobAdView.startdate = param.get("startdate").head.filterNot((x: Char) => x.isWhitespace).toLong
        jobAdView.enddate = param.get("enddate").head.filterNot((x: Char) => x.isWhitespace).toLong

        jobAdView.site_id = param.get("site_id").head.toInt
      }

    }

    return jobAdView
  }

}


