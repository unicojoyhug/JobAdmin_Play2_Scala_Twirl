package models

import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._


case class SiteView(id: Int, name: String, locale: String)

class JobAdView {
  var id: Option[Int] = None
  var title: String = _
  var logo: Option[String] = None
  var premium: Option[Boolean] = None
  var allow_personalized: Boolean = _
  var externallink: String = _
  var startdate: Long = _
  var enddate: Long = _
  var category_id: Option[Int] = None
  var category_name: Option[String] = None
  var site_id: Int = _
  var company_id: Int = _
  var company_name: String = _
}

case class JobAdForm (title: String, premium: Boolean, allow_personalized: Boolean, externallink:String,
                      startdate: Long, enddate: Long,
                      category_name: String, company_name: String)

object JobAdForm{
  val createJobAdForm = Form (
    mapping(
      "title" -> nonEmptyText,
      "premium" -> boolean,
      "allow_personalized" -> boolean,
      "externallink" -> text,
      "startdate" -> longNumber,
      "enddate" -> longNumber,
      "category_name" -> text,
      "company_name" -> text
    )(JobAdForm.apply)(JobAdForm.unapply)
  )
}



