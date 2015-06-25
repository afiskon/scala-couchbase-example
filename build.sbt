
lazy val commonSettings = Seq(
    organization := "me.eax",
    version := "0.1",
    scalaVersion := "2.11.6"
  )

lazy val sdk14 = (project in file("sdk14")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.couchbase.client" % "couchbase-client" % "1.4.9"
    )
  )

lazy val sdk21 = (project in file("sdk21")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.couchbase.client" % "java-client" % "2.1.3"
    )
  )

lazy val root = (project in file(".")).
  aggregate(sdk14, sdk21)
