package models

import play.api.data._
import play.api.data.Forms._


case class SiteView(id: Int, name: String, locale: String)

class JobAdView {
  var id: Int = _
  var title: String = _
  var logo: Option[String] = None
  var premium: Option[Boolean] = None
  var allow_personalized: Boolean = _
  var externallink: Option[String] = None
  var startdate: Long = _
  var enddate: Long = _
  var category_id: Option[Int] = None
  var category_name: Option[String] = None
  var site_id: Int = _
  var site_name: String = _
  var company_id: Int = _
  var company_name: String = _
}



case class JobAdForm (title: String, jobtype: String, externallink:String,
                      startdate: Long, enddate: Long,
                      category_id: Option[Int] = None, company_id: Int,
                      site_id: Int)

object JobAdForm{
  val createJobAdForm = Form (
    mapping(
      "title" -> nonEmptyText,
      "jobtype" -> nonEmptyText,
      "externallink" -> text,
      "startdate" -> longNumber,
      "enddate" -> longNumber,
      "category_id" -> optional(number),
      "company_id" -> number,
      "site_id" -> number
    )(JobAdForm.apply)(JobAdForm.unapply)
  )
}

class CompanyView {
  var id: Int = _
  var name: String = _
  var logo: String = _
  var specialAgreement: Boolean = _
}



