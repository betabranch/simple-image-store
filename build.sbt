organization := "com.betabranch"

name := "simple-image-store"

version := "0.1.0-SNAPSHOT"

seq(sbtappengine.Plugin.webSettings:  _*)

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "0.5.1",
  "net.databinder" %% "unfiltered-json" % "0.5.1",
  "net.databinder" %% "unfiltered-spec" % "0.5.1" % "test",
  "com.google.appengine" % "appengine-java-sdk" % "1.5.4",
  "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.4",
  "com.google.appengine" % "appengine-jsr107cache" % "1.5.4",
  "com.google.appengine" % "appengine-api-labs" % "1.5.4",
  "net.sf.jsr107cache" % "jsr107cache" % "1.1",
  "com.google.appengine" % "appengine-tools-sdk" % "1.5.4",
  "com.googlecode.objectify" % "objectify" % "3.0",
  "javax.persistence" % "persistence-api" % "1.0"
) ++ Seq( // local testing
  "javax.servlet" % "servlet-api" % "2.3" % "provided",
  "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "jetty"
)

resolvers ++= Seq(
 "jboss" at  "https://repository.jboss.org/nexus/content/groups/public/",
 "objectify-appengine" at "http://objectify-appengine.googlecode.com/svn/maven"
) 