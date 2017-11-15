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

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class FileUploadController @Inject()(cc: ControllerComponents, configuration: Configuration, ws: WSClient)(implicit executionContext: ExecutionContext)
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
  def upload(id: Int) = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>
    val fileOption = request.body.file("picture").map {
      case FilePart(key, filename, contentType, file) =>
        logger.info(s"key = ${key}, filename = ${filename}, contentType = ${contentType}, file = $file")


        val fileupload = ws.url(s"$url/jobs/pdf/2503").addHttpHeaders("X-API-KEY" -> api_key)
          .post(Source(FilePart(filename, "pdf", Option("application/pdf") , FileIO.fromPath(file.toPath)) :: DataPart("key", "value") :: List()))
        val result = Await.result(fileupload, Duration(10, SECONDS))

        result match {
          case x if 200 until 299 contains x.status.intValue => Ok("File uploaded")

          case resultCode =>
            println("#########################")
            println(s"File not uploaded" + resultCode)
            println("#########################")
            Ok("File failed")

        }


        val data = operateOnTempFile(file)
        data
    }

    Ok(s"file size = ${fileOption.getOrElse("no file")}")
  }



  /*def uploadPdf() = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { file =>
      val filename = file.filename
      val contentType = file.contentType
      file.ref.moveTo(Paths.get(s"/tmp/picture/$filename"), replace = true)

      val futureResponse = ws.url(s"$url/jobs/pdf/2495").addHttpHeaders("X-API-KEY" -> api_key).post(file)

          Ok("File uploaded")
      }.getOrElse {
        Redirect(routes.JobController.index("finanswatch.dk")).flashing(
          "error" -> "Missing file")
      }
    }
  }*/

/*  def upload = Action(parse.multipartFormData) { request =>
      request.body.file("picture").map { picture =>
      import java.io.File
        println("Something")

      val filename = picture.filename
      val contentType = picture.contentType
        println("file : " + filename)
        println("file type : " + contentType)
        println("file ref : " + picture.ref)

        picture.ref.moveTo(new File(s"/testupload/$filename"))

        //picture.ref.moveTo(Paths.get(s"/testupload/$filename"), replace = true)

      val fileupload = ws.url(s"$url/jobs/pdf/2503").addHttpHeaders("X-API-KEY" -> api_key)
        .post(Source(FilePart(filename, "pdf", Option("multipart/form-data"), FileIO.fromPath(picture.ref)) :: DataPart("key", "value") :: List()))
      val result = Await.result(fileupload, Duration(10, SECONDS))

      result match {
        case x if 200 until 299 contains x.status.intValue => Ok("File uploaded")

        case resultCode =>
        println("#########################")
        println(s"File not uploaded successfully " + resultCode)
        println("#########################")
          Ok("File failed")

      }



    }.getOrElse {
      Redirect(routes.JobController.index("finanswatch.dk", 1)).flashing(
        "error" -> "Missing file")
    }
  }*/
}
