package services

import play.api.Configuration
import scala.concurrent.Future

import models.Site

trait SiteService {
  def getAllSites(configuration: Configuration) : Future[List[Site]]
  def getMsg(): String
}
