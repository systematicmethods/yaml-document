name := """yaml-document"""

val versionNumber = "0.1.0"
version := versionNumber

organization := "com.systematicmethods"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.17",
  "org.apache.httpcomponents" % "httpmime" % "4.2.1",
  "ch.qos.logback" % "logback-classic" % "1.1.5",
  "net.sf.jopt-simple" % "jopt-simple" % "4.9",

   // test
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "net.jcazevedo" %% "moultingyaml" % "0.2" % "test"
)

//"org.scala-lang" % "scala-library" % "2.11.7",



