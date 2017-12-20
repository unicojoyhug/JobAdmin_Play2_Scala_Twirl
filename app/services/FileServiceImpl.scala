package services

import java.io.File
import java.nio.file.{Files, Path}
import javax.inject.Inject

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink}
import akka.util.ByteString
import play.api.libs.streams.Accumulator
import play.api.Logger
import play.api.libs.ws.WSResponse
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.FilePart
import play.core.parsers.Multipart.FileInfo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

/**
  * inspired by https://github.com/playframework/play-scala-fileupload-example
  * @param jobApiService
  */

class FileServiceImpl  @Inject()(jobApiService: JobApiService) extends FileService {

  override def uploadFile(file: Option[MultipartFormData.FilePart[File]], id: Int, caseName: String): Future[Try[Int]]= {

  file match {
      case Some(FilePart(key, filename, contentType, file)) =>
        Logger.info(s"key = ${key}, filename = ${filename}, contentType = ${contentType}, file = $file")

        for{
          fileUpload <- jobApiService.uploadFile(id, caseName, key, filename, contentType, file)
          result <- handleFileupload(fileUpload)
          _ <- operateOnTempFile(file)
        } yield result

      case None => Future.successful(Success(0))
    }
  }


  def handleFileupload(result: WSResponse):Future[Try[Int]] ={
    result match {
      case x if 200 until 299 contains x.status.intValue =>
        println("File uploaded")
        Future.successful(Success(x.status.intValue()))

      case resultCode =>
        println("#########################")
        println(s"File not uploaded : " + resultCode)
        println("#########################")
        println("File failed")
        Future.successful(Failure(new Exception(s"File not uploaded : " + resultCode)))
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

    Future.successful(size)

  }

}
