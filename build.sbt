import com.typesafe.sbt.packager.MappingsHelper.fromClasspath
import sbt.Keys.{libraryDependencies, name}

val sparkV = "2.4.3"

lazy val commonDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.9.2",
  "com.github.pureconfig" %% "pureconfig-enumeratum" % "0.9.2",
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "com.typesafe" % "config" % "1.3.3",
  "org.rogach" %% "scallop" % "3.1.5"
)

lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkV % "provided",
  "org.apache.spark" %% "spark-sql" % sparkV % "provided",
  "org.apache.spark" %% "spark-hive" % sparkV % "provided",
  "org.eclipse.jetty" % "jetty-servlet" % "9.4.14.v20181114"
)

lazy val commonSettings = Seq(
  organization := "com.power",
  version := "0.1.0-SNAPSHOT",
  fork in Test := true,
  scalaVersion := "2.11.12",
  test in assembly := {},
  javaOptions in Test ++= Seq("-Dconfig.file=conf/application.conf", "-Dspark.master=local[4]"),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  },
  scalacOptions := Seq(
    "-encoding", "UTF-8",
    "-deprecation",
    "-feature",
    "-language:higherKinds",
    "-Ywarn-dead-code",
    "-Xlint",
    "-Xexperimental"
  ),
  assemblyShadeRules in assembly := Seq(ShadeRule.rename("shapeless.**" -> "new_shapeless.@1").inAll),
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    name:="core",
    libraryDependencies:=commonDependencies)

lazy val spark = (project in file("spark"))
  .dependsOn(core)
  .settings(
    commonSettings,
    name:="spark",
    libraryDependencies:=(sparkDependencies++commonDependencies)
  )

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name:="power",
    packageName in Universal := packageName.value + "-" + version.value,
    mappings in Universal ++= fromClasspath((managedClasspath in Runtime).value, "lib", _ => true),
    mappings in Universal ++= Seq(
      (core / Compile / packageBin).value,
      (spark / Compile / packageBin).value
    ).map(jar => jar -> ("lib/" + jar.getName))
  )
  .enablePlugins(UniversalPlugin)
  .aggregate(core, spark)
