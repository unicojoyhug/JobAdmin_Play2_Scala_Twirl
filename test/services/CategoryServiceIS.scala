import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec

import play.api.mvc.Results
import services.CategoryService

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class CategoryServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder {
  val sut = injector.instanceOf[CategoryService]

  "#getAllJobs" should {
    "return 0 jobs" in {
      val lists = sut.getAllCategoriesBySite("finanswatch.dk")
      val categoryLength = Await.result(lists, Duration(10, SECONDS)).length
      withClue("Site length should be: ") {
        categoryLength mustBe 14
      }
    }
  }
}
