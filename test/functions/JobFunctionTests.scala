import akka.actor.Status.Success
import models.{JobAd, JobAdView}
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.Results
import services.JobAdService

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, SECONDS}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._

class JobFunctionTests extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder {
  val sut = injector.instanceOf[JobAdService]
  val ws = injector.instanceOf[WSClient]

  "#convertJobAdView" should {

      val jobAdView = new JobAdView()
      jobAdView.title = "TestJob"
      jobAdView.externallink = Some("Test External link")
      jobAdView.startdate = 1509954397228l
      jobAdView.enddate = 1509954397228l
      jobAdView.site_id = 1
      jobAdView.company_id = 1
      jobAdView.allow_personalized = false


      def convertJobAdView(jobAdView: JobAdView): JobAd = {
        var jobAd = new JobAd(

          None,
          jobAdView.title,
          jobAdView.logo,
          "Juyoung Choi",
          jobAdView.premium,
          jobAdView.externallink.getOrElse(""),
          jobAdView.startdate,
          jobAdView.enddate,
          DateTime.now().getMillis,
          DateTime.now().getMillis,
          jobAdView.category_id,
          jobAdView.site_id,
          jobAdView.company_id,
          jobAdView.allow_personalized

        )


        return jobAd
      }

    "return a correct JobAd object" in {

      val result = convertJobAdView(jobAdView)

      val now = DateTime.now().getMillis
      val jobad = JobAd(None, "TestJob", None, "Juyoung Choi", None, "Test External link", 1509954397228l, 1509954397228l, now, now, None, 1, 1, false)
      result.title mustBe jobad.title

    }



    "parse correct json" in {
      val jobad = JobAd(None, "TestJob", None, "Juyoung Choi", None, "Test External link", 1509954397228l, 1509954397228l, 1509955566687l, 1509955566687l, None, 1, 1, false)
      implicit val format = Json.writes[JobAd]

      val json = Json.toJson[JobAd](jobad)

      System.out.println(json)

      val jobsJson: JsValue = Json.parse(

        """{
          "title":"TestJob",
          "createdby":"Juyoung Choi",
          "externallink":"Test External link",
          "trackinglink":"Test Tracking link",
          "startdate":1509954397228,
          "enddate":1509954397228,
          "createdate":1509955566687,
          "updatedate":1509955566687,
          "site_id":1,"company_id":1,
          "allow_personalized":false}
        """)

      json mustBe jobsJson

    }


    def convertJobAdViewToJson(jobAdView: JobAdView): JsValue = {
      implicit val format = Json.writes[JobAd]

      var jobAd = new JobAd(
        None,
        jobAdView.title,
        jobAdView.logo,
        "Juyoung Choi",
        jobAdView.premium,
        jobAdView.externallink.getOrElse(""),
        jobAdView.startdate,
        jobAdView.enddate,
        1509955566687l,
        1509955566687l,
        jobAdView.category_id,
        jobAdView.site_id,
        jobAdView.company_id,
        jobAdView.allow_personalized

      )


      return Json.toJson[JobAd](jobAd)
    }

    "#convertJobAdToJson" in {
      val jobAdView = new JobAdView()
      jobAdView.title = "TestJob"
      jobAdView.externallink = Some("Test External link")
      jobAdView.startdate = 1509954397228l
      jobAdView.enddate = 1509954397228l
      jobAdView.site_id = 1
      jobAdView.company_id = 1
      jobAdView.allow_personalized = false

      val jobsJson: JsValue = Json.parse(

        """{
          "title":"TestJob",
          "createdby":"Test",
          "externallink":"Test External link",
          "trackinglink":"Test Tracking link",
          "startdate":1509954397228,
          "enddate":1509954397228,
          "createdate":1509955566687,
          "updatedate":1509955566687,
          "site_id":1,"company_id":1,
          "allow_personalized":false}
        """)

      val result = convertJobAdViewToJson(jobAdView)

      result mustBe jobsJson
    }
  }

  "#createJobAd" should {
    val jobsJson: JsValue = Json.parse(

      """{
          "title":"TestJob",
          "createdby":"Test",
          "externallink":"Test External link",
          "trackinglink":"Test Tracking link",
          "startdate":1509954397228,
          "enddate":1509954397228,
          "createdate":1509955566687,
          "updatedate":1509955566687,
          "site_id":1,"company_id":1,
          "allow_personalized":false}
        """)

    val futureResponse = ws.url(s"http://localhost:9000/v1/admin/jobs").addHttpHeaders("X-API-KEY" -> "1234").post(jobsJson).map{
      result => (result.json \ "job_id").as[Int]
    }

    "getResponse" in {

      futureResponse mustBe Future(2492)

    }

  }
}
