package services

import java.io.File

import play.api.mvc.MultipartFormData

trait FileService {

  def uploadFile(file: Option[MultipartFormData.FilePart[File]], id:Int, caseName: String)

}
