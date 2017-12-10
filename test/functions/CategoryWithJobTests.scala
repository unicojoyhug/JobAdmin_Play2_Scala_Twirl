import models.{Category, JobAdView}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import services.{CategoryService, JobAdService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.ExecutionContext.Implicits.global

class CategoryWithJobTests extends PlaySpec with Results with BeforeAndAfter with IntegrationAppBuilder {
  val sut = injector.instanceOf[JobAdService]
  val categoryService = injector.instanceOf[CategoryService]
  val ws = injector.instanceOf[WSClient]


  "#getCategoryWithJob" should {

    "getResponse" in {

      val site = "finanswatch.dk"

      val list = getList(site)

      val result = Await.result(list, Duration(10, SECONDS))

      result.foreach(println)

      result.length mustBe 14
    }

    def getList(site:String): Future[List[CategoryWithNumberOfJobsView]] = {
      for{
        joblist <- sut.getAllJobAdViews(site)
        categorylist <- categoryService.getAllCategoriesBySite(site)
      }yield getCategoryWithNumberOfJobAds (joblist,site,categorylist)
    }

    case class CategoryWithNumberOfJobsView(site: String, categoryId: Int, categoryName: String, jobNumber: Int)

    def getCategoryWithNumberOfJobAds(joblist: List[JobAdView], site: String, categoryList: List[Category]): List[CategoryWithNumberOfJobsView] = {

      var result = ListBuffer[CategoryWithNumberOfJobsView]()

      for(item <- categoryList){
        var jobNumber = 0
        jobNumber = joblist.count(job => job.category_id.getOrElse(-1) == item.id)
        val categoryWithJobNumber = CategoryWithNumberOfJobsView(
          site,
          item.id,
          item.name,
          jobNumber
        )
        result += categoryWithJobNumber
      }

      return result.toList
    }
  }

}
