package models


case class SiteView(id: Int, name: String, locale: String)


class JobAdView {
  var id: Option[Int] = None
  var title: String = _
  var logo: Option[String] = None
  var createdby: String = _
  var premium: Option[Boolean] = None
  var allow_personalized: Boolean = _
  var externallink: String = _
  var trackinglink: String = _
  var startdate: Long = _
  var enddate: Long = _
  var category_id: Option[Int] = None
  var category_name: Option[String] = None
  var site_id: Int = _
  var company_id: Int = _
  var company_name: String = _
}

