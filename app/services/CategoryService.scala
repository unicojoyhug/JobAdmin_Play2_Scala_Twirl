package services

import models.{Category}
import play.api.Configuration

import scala.concurrent.Future

trait CategoryService {

  def getAllCategoriesBySite(configuration: Configuration, site:String) : Future[List[Category]]
}
