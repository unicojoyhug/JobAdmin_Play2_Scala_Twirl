import models.JobAdView
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results
import services.JobAdService

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class JobAdServiceIS extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder{
  val sut = injector.instanceOf[JobAdService]


  val jobAdView = new JobAdView()
  jobAdView.title = "TestJob"
  jobAdView.externallink = Some("Test Edit")
  jobAdView.startdate = 1509954397228l
  jobAdView.enddate = 1509954397228l
  jobAdView.site_id = 12
  jobAdView.company_id = 1
  jobAdView.allow_personalized = false

  "#getAllJobs" should {
    "return 0 jobs" in {
      val lists = sut.getAllJobAdViews("WatchAnbefaler")
      val jobLength = Await.result(lists, Duration(10, SECONDS)).length
      withClue("Joblist length should be: "){
        jobLength mustBe 0
      }
    }
  }

  "#createJob" should {
    "return jobId" in {
      val result = sut.createJobAd(jobAdView)
      val id = Await.result(result, Duration(10, SECONDS))

      withClue("New Job Id from API should be: "){
        id mustBe 2495
      }
    }
  }

  "#deleteJob" should {
    "return Ok" in {
      val result = sut.deleteJobAd(2500)
      val status = Await.result(result, Duration(10, SECONDS))

      withClue("Response should be true: "){
        status mustBe "Ok"
      }
    }
  }

  "#editJob" should {
    "return Ok" in {
      jobAdView.id = 2512
      val result = sut.editJobAd(jobAdView)
      val jobAdId = Await.result(result, Duration(10, SECONDS))

      withClue("Response should be true: "){
        jobAdId mustBe 2512
      }
    }
  }

}
