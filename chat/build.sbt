name := "themis-scala"
version := "1.0"
scalaVersion := "3.5.1"

libraryDependencies ++= Seq(
  "com.datastax.oss" % "java-driver-core" % "4.15.0",
  "com.typesafe" % "config" % "1.4.2",
  "org.slf4j" % "slf4j-api" % "2.0.7",
  "ch.qos.logback" % "logback-classic" % "1.4.7",
  "info.picocli" % "picocli" % "4.7.3"
)