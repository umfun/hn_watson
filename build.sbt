name := "hn_watson"
version := "1.0"
scalaVersion := "2.11.4"

// Resolvers
resolvers ++= Seq(
  "SoftwareMill Public Releases" at "https://nexus.softwaremill.com/content/repositories/releases/",
  "SoftwareMill Public Snapshots" at "https://nexus.softwaremill.com/content/repositories/snapshots/"
)

val macwireVersion = "0.8"
val macwire = Seq(
  "com.softwaremill.macwire" %% "macros" % macwireVersion,
  "com.softwaremill.macwire" %% "runtime" % macwireVersion
)

val slf4jVersion = "1.7.6"
val logging = Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
)

val jodaTime = Seq(
  "joda-time" % "joda-time" % "2.0",
  "org.joda" % "joda-convert" % "1.2"
)

val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"
val testingDeps = Seq(scalaTest)

val jsoup = "org.jsoup" % "jsoup" % "1.7.3"

val casbah = "org.mongodb" %% "casbah" % "2.7.4" exclude(org = "org.scala-lang", name = "scala-library")
val salat = "com.novus" %% "salat" % "1.9.9"

val lawn = "com.softwaremill.thegarden" %% "lawn" % "0.0.25-SNAPSHOT"

val sprayClient = "io.spray" %% "spray-client" % "1.3.2"

def akkaModule(name : String) = {
  val akkaVersion = "2.3.7"
  "com.typesafe.akka" %% s"akka-$name" % akkaVersion
}
val akka = (akkaModule("testkit") % "test") :: (List("actor", "slf4j") map akkaModule)

libraryDependencies ++= logging ++ jodaTime ++ testingDeps ++
  Seq(casbah, salat, jsoup, sprayClient, lawn) ++ akka
