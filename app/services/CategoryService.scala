package services

import models.{Category}
import play.api.Configuration

import scala.concurrent.Future

trait CategoryService {

  def getAllCategoriesBySite(site:String) : Future[List[Category]]
}
