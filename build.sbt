name := "whereis-utrecht"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-client"  % sprayV,
    "io.spray" %%  "spray-json" % "1.3.2",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "io.argonaut" %% "argonaut" % "6.0.4",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}
    