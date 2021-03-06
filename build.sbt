name := "job_admin_twirl"
 
version := "1.0-SNAPSHOT"
maintainer := "Juyoung Choi"
dockerExposedPorts in Docker := Seq(8080, 8999)
      
lazy val `job_admin_twirl` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
libraryDependencies ++= Seq(
  filters,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "de.leanovate.play-mockws" %% "play-mockws" % "2.6.2" % Test,
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B4",
  "org.webjars" % "bootstrap" % "4.0.0-alpha.6-1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1",
  "org.webjars" % "font-awesome" % "4.7.0",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0" exclude("org.webjars", "bootstrap")
)


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

routesGenerator := InjectedRoutesGenerator