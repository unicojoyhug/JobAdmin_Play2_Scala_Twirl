
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import services.JobAdService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, SECONDS}

class JobSortingFunctionTests extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder {
  val sut = injector.instanceOf[JobAdService]
  val ws = injector.instanceOf[WSClient]

  "#checkDate" should {
    var yesterday = DateTime.now().minusDays(1).getMillis
    var today = (new DateTime).withYear(2017)
                              .withMonthOfYear(11)
                              .withDayOfMonth(27).getMillis
    var tomorrow = DateTime.now().plusDays(1).getMillis

    val list = List(yesterday, today, tomorrow)
    var checkDate = DateTime.now().getMillis

    val filteredList = list.filter(_ > yesterday)

    "filtered list" in {
      filteredList.size mustBe 2

    }

  }

  "#getActiveJobs" should {
    val list = sut.getAllJobAdViews("finanswatch.dk")

    "getAllActiveJobs" in {
      var today = DateTime.now().getMillis
      val result = Await.result(list, Duration(10, SECONDS))

      val test = result.filter(_.enddate > today)
      val activeJobs = test.filter(_.startdate < today)
      activeJobs.toString()

      activeJobs.length mustBe 31

      }

    }

}
