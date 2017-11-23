package services

import java.io.File

import play.api.mvc.MultipartFormData
import play.core.parsers.Multipart.FilePartHandler

import scala.concurrent.Future
import scala.util.Try

trait FileService {

  def uploadFile(file: Option[MultipartFormData.FilePart[File]], id:Int, caseName: String): Future[Try[Int]]
  def handleFilePartAsFile: FilePartHandler[File]
}
