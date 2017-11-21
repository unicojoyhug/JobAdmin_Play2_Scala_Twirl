package services

import java.io.File

import play.api.mvc.MultipartFormData
import play.core.parsers.Multipart.FilePartHandler

trait FileService {

  def uploadFile(file: Option[MultipartFormData.FilePart[File]], id:Int, caseName: String)
  def handleFilePartAsFile: FilePartHandler[File]
}
