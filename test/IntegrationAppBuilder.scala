import java.util.Calendar

import org.scalatest.{BeforeAndAfterAll, Suite}
import play.api.Mode
import play.api.inject.guice.GuiceApplicationBuilder

trait IntegrationAppBuilder extends BeforeAndAfterAll {
  this: Suite =>
  lazy val injector = new GuiceApplicationBuilder().in(Mode.Test)
    .bindings(new Module)
    .injector()

  def nowAsTimestamp(daysDiff: Int = 0) = {
    val utilDate = new java.util.Date()
    val now = Calendar.getInstance()
    now.setTime(utilDate)
    now.add(Calendar.DATE, daysDiff)
    val nowDiff = new java.sql.Timestamp(now.getTimeInMillis())
    nowDiff
  }
}

