package controllers

import java.io.File
import java.nio.file.{Files, Path, Paths}
import javax.inject.Inject

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import play.api.{Configuration, Logger}
import play.api.libs.streams.Accumulator
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, FilePart}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.core.parsers.Multipart.FileInfo
import services.FileService

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class FileUploadController @Inject()(cc: ControllerComponents, configuration: Configuration, ws: WSClient, fileService: FileService)(implicit executionContext: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  val api_key: String = configuration.get[String]("security.apikeys")
  val url: String = configuration.get[String]("job_api.url")

  private val logger = Logger(this.getClass)


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
          logger.info(s"count = $count, status = $status")
          FilePart(partName, filename, contentType, path.toFile)
      }
  }

  /**
    * A generic operation on the temporary file that deletes the temp file after completion.
    */
  private def operateOnTempFile(file: File) = {
    val size = Files.size(file.toPath)
    logger.info(s"size = ${size}")
    Files.deleteIfExists(file.toPath)
    size
  }

  /**
    * Uploads a multipart file as a POST request.
    *
    * @return
    */
  def upload() = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>
    //def upload(caseName: String, key: String) = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>
    //def upload(caseName: String, key: String, id: Int) = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>

    val caseName = "jobs"
    val key ="pdf"
    val id = 2576

    logger.info(s"caseName = ${caseName}")

    fileService.uploadFile(request.body.file(key), id, caseName)

    Ok("Done")


   /* val fileOption = request.body.file(key).map {
      case FilePart(key, filename, contentType, file) =>
        logger.info(s"key = ${key}, filename = ${filename}, contentType = ${contentType}, file = $file")

        ///v1/admin/companies/image/:id
        ///v1/admin/jobs/pdf/:id
        ///v1/admin/jobs/image/:id

        val fileupload = ws.url(s"$url/$caseName/$key/$id").addHttpHeaders("X-API-KEY" -> api_key)
          .post(Source(FilePart(key, s"${filename}", Option(s"${contentType}") , FileIO.fromPath(file.toPath)) :: DataPart("key", "value") :: List()))

        fileupload map {
          result => result match {
            case x if 200 until 299 contains x.status.intValue => println("File uploaded")

            case resultCode =>
              println("#########################")
              println(s"File not uploaded : " + resultCode)
              println("#########################")
              println("File failed")

          }
            val data = operateOnTempFile(file)

            println("DONE fileupload map")

        }

    }
    Ok(s"file size = ${fileOption.getOrElse("no file")}")*/

  }

}
