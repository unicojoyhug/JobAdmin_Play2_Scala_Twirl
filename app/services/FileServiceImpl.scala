package services

import java.io.File
import java.nio.file.{Files, Path}
import javax.inject.Inject

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import play.api.libs.streams.Accumulator
import play.api.{Configuration, Logger}
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.{DataPart, FilePart}
import play.core.parsers.Multipart.FileInfo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileServiceImpl  @Inject()(ws: WSClient, configuration: Configuration ) extends FileService {
  val api_key: String = configuration.get[String]("security.apikeys")
  val url: String = configuration.get[String]("job_api.url")

  override def uploadFile(file: Option[MultipartFormData.FilePart[File]], id: Int, caseName: String): Unit = {

    val fileOption = file map {
      case FilePart(key, filename, contentType, file) =>
        Logger.info(s"key = ${key}, filename = ${filename}, contentType = ${contentType}, file = $file")

        // /v1/admin/companies/image/:id
        // /v1/admin/jobs/pdf/:id
        // /v1/admin/jobs/image/:id
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

            operateOnTempFile(file)

        }


    }
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
  override def handleFilePartAsFile: FilePartHandler[File] = {
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

  /**
    * A generic operation on the temporary file that deletes the temp file after completion.
    */
  private def operateOnTempFile(file: File) = {
    val size = Files.size(file.toPath)
    Logger.info(s"size = ${size}")
    Files.deleteIfExists(file.toPath)
    size
  }

}
