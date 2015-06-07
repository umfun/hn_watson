import sbt._
import sbt.Keys._

object Dependencies {
  val macwireVersion = "1.0.1"
  val macwire = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireVersion,
    "com.softwaremill.macwire" %% "runtime" % macwireVersion
  )

  val slf4jVersion = "1.7.6"
  val logging = Seq(
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  )

  val jodaTime = Seq(
    "joda-time" % "joda-time" % "2.0",
    "org.joda" % "joda-convert" % "1.2"
  )

  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  val testing = Seq(scalaTest)

  val jsoup = "org.jsoup" % "jsoup" % "1.7.3"

  val casbah = "org.mongodb" %% "casbah" % "2.7.4" exclude(org = "org.scala-lang", name = "scala-library")
  val salat = "com.novus" %% "salat" % "1.9.9"

  val gardenVersion = "0.0.33"
  val lawn = "me.maciejb.garden" %% "garden-lawn" % gardenVersion

  val sprayClient = "io.spray" %% "spray-client" % "1.3.2"

  def akkaModule(name: String) = {
    val akkaVersion = "2.3.11"
    "com.typesafe.akka" %% s"akka-$name" % akkaVersion
  }
  val akka = (akkaModule("testkit") % "test") :: (List("actor", "slf4j") map akkaModule)

  val json4sVersion = "3.2.11"
  val json4s = Seq(
    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "org.json4s" %% "json4s-ext" % json4sVersion,
    "me.maciejb.garden" %% "garden-json4s" % gardenVersion
  )

  val spark = "org.apache.spark" %% "spark-core" % "1.3.1" exclude("org.slf4j", "slf4j-log4j12")
}

object build extends sbt.Build {

  import Dependencies._

  val commonSettings = Seq(
    organization := "me.maciejb.hnwatson",
    version := "1.0",
    scalaVersion := "2.11.6",
    resolvers ++= Seq(
      "Garden repository" at "https://dl.bintray.com/maciej/maven/"
    )
  )

  lazy val commons = project.in(file("commons")).settings(
    commonSettings: _*
  )

  lazy val sparkAnalytics = project.in(file("spark-analytics")).settings(
    libraryDependencies ++= logging ++ testing ++ json4s ++ Seq(spark, jsoup)
  ).settings(commonSettings: _*)
    .dependsOn(commons)

  lazy val oldPipeline = project.in(file("old-pipeline")).settings(
    libraryDependencies ++= logging ++ jodaTime ++ testing ++
      Seq(casbah, salat, jsoup, sprayClient, lawn) ++ akka
  ).settings(commonSettings: _*)
    .dependsOn(commons)

  lazy val root = project.in(file(".")).settings(
    name := "hn_watson",
    libraryDependencies ++= logging ++ jodaTime ++ testing ++
      Seq(casbah, salat, jsoup, sprayClient, lawn) ++ akka
  ).settings(commonSettings: _*)
    .aggregate(commons, sparkAnalytics, oldPipeline)

}
