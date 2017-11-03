import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec

import play.api.mvc.Results
import services.JobAdService

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class JobAdServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder{
  val sut = injector.instanceOf[JobAdService]

  "#getAllJobs" should {
    "return 0 jobs" in {
      val lists = sut.getAllJobAdViews("WatchAnbefaler")
      val jobLength = Await.result(lists, Duration(10, SECONDS)).length
      withClue("Joblist length should be: "){
        jobLength mustBe 0
      }
    }
  }
}
