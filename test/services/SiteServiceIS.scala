import org.scalatest.BeforeAndAfter
import org.scalatestplus.play._
import play.api.mvc._

import services.SiteService

import scala.concurrent.Await
import scala.concurrent.duration._


class SiteServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder{
  val sut = injector.instanceOf[SiteService]

  "#getAllSites" should {
    "return 13 sites" in {
      val lists = sut.getAllSites()
      val siteLength = Await.result(lists, Duration(10, SECONDS)).length
      withClue("Site length should be: "){
        siteLength mustBe 13
      }
    }
  }
}
