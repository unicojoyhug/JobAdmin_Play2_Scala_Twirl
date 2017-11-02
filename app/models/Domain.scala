package models

case class JobApiException(exc: String) extends Exception(exc)

case class Site(id: Int, name: String, locale: String, createdate: Long, updatedate: Long)


case class JobAd(id: Option[Int] = None, title: String, logo: Option[String] = None,
                 createdby: String, premium: Option[Boolean] = None, externallink: String,
                 trackinglink: String, startdate: Long, enddate: Long, createdate: Long, updatedate: Long,
                 category_id: Option[Int] = None, site_id: Int, company_id: Int, allow_personalized: Boolean)

case class Company(id: Int, name: String, createdate: Long, updatedate: Long, logo: String)


case class Category(id: Int, name: String, createdate: Long, updatedate: Long, site_id: Int)
