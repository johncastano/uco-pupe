import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.12.7",
      name := "UCO-pupe",
      fork := true,
      fork in Test := true,
      fork in IntegrationTest := true,
      coverageEnabled := true,
      coverageMinimum := 40,
      coverageFailOnMinimum := true,
      coverageHighlighting := true,
      libraryDependencies ++= Seq(
        scalaTest % Test
      ),
      scalacOptions ++= Seq(
        "-deprecation",
        "-encoding", "utf-8",
        "-explaintypes",
        "-feature",
        "-language:existentials",
        "-language:experimental.macros",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-unchecked",
        "-Xcheckinit",
        "-Xfatal-warnings",
        "-Xfuture",
        "-Xlint:adapted-args",
        "-Xlint:by-name-right-associative",
        "-Xlint:constant",
        "-Xlint:delayedinit-select",
        "-Xlint:doc-detached",
        "-Xlint:inaccessible",
        "-Xlint:infer-any",
        "-Xlint:missing-interpolator",
        "-Xlint:nullary-override",
        "-Xlint:nullary-unit",
        "-Xlint:option-implicit",
        "-Xlint:package-object-classes",
        "-Xlint:poly-implicit-overload",
        "-Xlint:private-shadow",
        "-Xlint:stars-align",
        "-Xlint:type-parameter-shadow",
        "-Xlint:unsound-match",
        "-Yno-adapted-args",
        "-Ypartial-unification",
        "-Ywarn-dead-code",
        "-Ywarn-extra-implicit",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit",
        "-Ywarn-numeric-widen",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates",
        "-Ywarn-value-discard"
      ),
      scalacOptions in (Compile, console) ~= (_.filterNot(Set(
        "-Ywarn-unused:imports",
        "-Xfatal-warnings"
      )))
    ))
  )
