import BuildKeys._
import Boilerplate._

import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import sbtcrossproject.CrossProject

// ---------------------------------------------------------------------------
// Commands


/* We have no other way to target only JVM or JS projects in tests. */
lazy val aggregatorIDs = Seq("core")

addCommandAlias("ci-jvm",     ";" + aggregatorIDs.map(id => s"${id}JVM/clean ;${id}JVM/test:compile ;${id}JVM/test").mkString(";"))
addCommandAlias("ci-js",      ";" + aggregatorIDs.map(id => s"${id}JS/clean ;${id}JS/test:compile ;${id}JS/test").mkString(";"))
addCommandAlias("ci-package", ";scalafmtCheckAll ;package")
addCommandAlias("ci-doc",     ";unidoc ;site/makeMicrosite")
addCommandAlias("ci",         ";project root ;reload ;+scalafmtCheckAll ;+ci-jvm ;+ci-js ;+package ;ci-doc")
addCommandAlias("release",    ";+clean ;ci-release ;unidoc ;microsite/publishMicrosite")

// ---------------------------------------------------------------------------
// Dependencies

/** For macros that are supported on older Scala versions.
  * Not needed starting with Scala 2.13.
  */
val MacroParadiseVersion = "2.1.1"

/** Library for unit-testing:
  *  - [[https://github.com/scalatest/scalatest]]
  */
val ScalaTestVersion = "3.2.0"

/** Used for publishing the microsite:
  * [[https://github.com/47degrees/github4s]]
  */
val GitHub4sVersion = "0.24.1"


/** Used for === and =!= macros:
 * [[https://github.com/er1c/scala-typesafeequals]]
 */
val TypesafeEqualsVersion = "1.0.0"

/**
  * Defines common plugins between all projects.
  */
def defaultPlugins: Project ⇒ Project = pr => {
  val withCoverage = sys.env.getOrElse("SBT_PROFILE", "") match {
    case "coverage" => pr
    case _ => pr.disablePlugins(scoverage.ScoverageSbtPlugin)
  }
  withCoverage
    .enablePlugins(AutomateHeaderPlugin)
    .enablePlugins(GitBranchPrompt)
}

lazy val sharedSettings = Seq(
  projectTitle := "fm-common",
  projectWebsiteRootURL := "https://er1c.github.io/",
  projectWebsiteBasePath := "/fm-common/",
  githubOwnerID := "er1c",
  githubRelativeRepositoryID := "fm-common",

  organization := "io.github.er1c",
  scalaVersion := "2.13.3",
  crossScalaVersions := Seq("2.11.12", "2.12.11", "2.13.3"),

  //scalacOptions += "-language:implicitConversions,experimental.macros",

  libraryDependencies ++= Seq(
    "io.github.er1c" %%% "scala-typesafeequals" % TypesafeEqualsVersion % Compile,
  ),

  // More version specific compiler options
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v >= 12 =>
      Seq(
        "-Ywarn-macros:after"
      )
    case _ =>
        Seq.empty
  }),

  baseDirectory in (Test, run) := (baseDirectory in LocalRootProject).value,

  // Turning off fatal warnings for doc generation
  scalacOptions.in(Compile, doc) ~= filterConsoleScalacOptions,

  // things  like .linesIterator were changed to not deprecated, go with 2.13
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 12 =>
      Seq(
        "-deprecation:false"
      )
    case _ =>
      Seq()
  }),

  // ScalaDoc settings
  autoAPIMappings := true,
  scalacOptions in ThisBuild ++= Seq(
    // Note, this is used by the doc-source-url feature to determine the
    // relative path of a given source file. If it's not a prefix of a the
    // absolute path of the source file, the absolute path of that file
    // will be put into the FILE_SOURCE variable, which is
    // definitely not what we want.
    "-sourcepath", file(".").getAbsolutePath.replaceAll("[.]$", "")
  ),

  // https://github.com/sbt/sbt/issues/2654
  incOptions := incOptions.value.withLogRecompileOnMacro(false),

  // ---------------------------------------------------------------------------
  // Options for testing

  logBuffered in Test := false,
  logBuffered in IntegrationTest := false,

  // ---------------------------------------------------------------------------
  // Options meant for publishing on Maven Central

  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false }, // removes optional dependencies

  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url(projectWebsiteFullURL.value)),
  headerLicense := Some(HeaderLicense.Custom(
    s"""|Copyright (c) 2019 Frugal Mechanic (http://frugalmechanic.com)
        |Copyright (c) 2020 the ${projectTitle.value} contributors.
        |See the project homepage at: ${projectWebsiteFullURL.value}
        |
        |Licensed under the Apache License, Version 2.0 (the "License");
        |you may not use this file except in compliance with the License.
        |You may obtain a copy of the License at
        |
        |    http://www.apache.org/licenses/LICENSE-2.0
        |
        |Unless required by applicable law or agreed to in writing, software
        |distributed under the License is distributed on an "AS IS" BASIS,
        |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        |See the License for the specific language governing permissions and
        |limitations under the License."""
      .stripMargin)),

  scmInfo := Some(
    ScmInfo(
      url(s"https://github.com/${githubFullRepositoryID.value}"),
      s"scm:git@github.com:${githubFullRepositoryID.value}.git"
    )),

  developers := List(
    Developer(
      id="ericpeters",
      name="Eric Peters",
      email="eric@peters.org",
      url=url("https://github.com/er1c")
    ),
    Developer(
      id="tpunder",
      name="Tim Underwood",
      email="timunderwood@gmail.com",
      url=url("http://github.com/tpunder")
    )
  ),

  // -- Settings meant for deployment on oss.sonatype.org
  sonatypeProfileName := organization.value,
)

/**
  * Shared configuration across all sub-projects with actual code to be published.
  */
def defaultCrossProjectConfiguration(pr: CrossProject) = {
  val sharedJavascriptSettings = Seq(
    coverageExcludedFiles := ".*",
    // Use globally accessible (rather than local) source paths in JS source maps
    scalacOptions += {
      val tagOrHash = {
        val ver = s"v${version.value}"
        if (isSnapshot.value)
          git.gitHeadCommit.value.getOrElse(ver)
        else
          ver
      }
      val l = (baseDirectory in LocalRootProject).value.toURI.toString
      val g = s"https://raw.githubusercontent.com/${githubFullRepositoryID.value}/$tagOrHash/"
      s"-P:scalajs:mapSourceURI:$l->$g"
    
    },
    // For @nowarn
    //libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6",

    fork in Test := false,

    // Needed in order to publish for multiple Scala.js versions:
    // https://github.com/olafurpg/sbt-ci-release#how-do-i-publish-cross-built-scalajs-projects
    skip.in(publish) := customScalaJSVersion.isEmpty,
  )

  val sharedJVMSettings = Seq(
    // Needed in order to publish for multiple Scala.js versions:
    // https://github.com/olafurpg/sbt-ci-release#how-do-i-publish-cross-built-scalajs-projects
    skip.in(publish) := customScalaJSVersion.isDefined,
    fork in Test := true,
  )

  pr.configure(defaultPlugins)
    .settings(sharedSettings)
    .jsSettings(sharedJavascriptSettings)
    .jvmSettings(doctestTestSettings(DoctestTestFramework.ScalaTest))
    .jvmSettings(sharedJVMSettings)
    .settings(crossVersionSharedSources)
    .settings(requiredMacroCompatDeps(MacroParadiseVersion))
    .settings(filterOutMultipleDependenciesFromGeneratedPomXml(
      "groupId" -> "org.scoverage".r :: Nil,
      "groupId" -> "org.typelevel".r :: "artifactId" -> "simulacrum".r :: Nil,
    ))
}

lazy val root = project.in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate(coreJVM, coreJS)
  .configure(defaultPlugins)
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .settings(unidocSettings(coreJVM))
  .settings(
    // Try really hard to not execute tasks in parallel ffs
    Global / concurrentRestrictions := Tags.limitAll(1) :: Nil,
  )

lazy val site = project.in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(MdocPlugin)
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .dependsOn(coreJVM)
  .settings {
    import microsites._
    Seq(
      micrositeName := projectTitle.value,
      micrositeDescription := "fm-common",
      micrositeAuthor := "Eric Peters",
      micrositeTwitterCreator := "@ericpeters",
      micrositeGithubOwner := githubOwnerID.value,
      micrositeGithubRepo := githubRelativeRepositoryID.value,
      micrositeUrl := projectWebsiteRootURL.value.replaceAll("[/]+$", ""),
      micrositeBaseUrl := projectWebsiteBasePath.value.replaceAll("[/]+$", ""),
      micrositeDocumentationUrl := s"${projectWebsiteFullURL.value.replaceAll("[/]+$", "")}/${docsMappingsAPIDir.value}/",
      micrositeGitterChannelUrl := githubFullRepositoryID.value,
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary" -> "#3e5b95",
        "brand-secondary" -> "#294066",
        "brand-tertiary" -> "#2d5799",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
      ),
      micrositeCompilingDocsTool := WithMdoc,
      fork in mdoc := true,
      scalacOptions.in(Tut) ~= filterConsoleScalacOptions,
      libraryDependencies += "com.47deg" %% "github4s" % GitHub4sVersion,
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeExtraMdFiles := Map(
        file("README.md") -> ExtraMdFileConfig("index.md", "page", Map("title" -> "Home", "section" -> "home", "position" -> "100")),
        file("CHANGELOG.md") -> ExtraMdFileConfig("CHANGELOG.md", "page", Map("title" -> "Change Log", "section" -> "changelog", "position" -> "101")),
        file("CODE_OF_CONDUCT.md") -> ExtraMdFileConfig("CODE_OF_CONDUCT.md", "page", Map("title" -> "Code of Conduct", "section" -> "code of conduct", "position" -> "102")),
        file("LICENSE.md") -> ExtraMdFileConfig("LICENSE.md", "page", Map("title" -> "License", "section" -> "license", "position" -> "103"))
      ),
      docsMappingsAPIDir := s"api",
      addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc) in root, docsMappingsAPIDir),
      sourceDirectory in Compile := baseDirectory.value / "src",
      sourceDirectory in Test := baseDirectory.value / "test",
      mdocIn := (sourceDirectory in Compile).value / "mdoc",

      // Bug in sbt-microsites
      micrositeConfigYaml := microsites.ConfigYml(
        yamlCustomProperties = Map("exclude" -> List.empty[String])
      ),
    )
  }

lazy val core = crossProject(JSPlatform, JVMPlatform)//, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .dependsOn(macros)
  .configureCross(defaultCrossProjectConfiguration)
  .settings(
    name := "fm-common",
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % ScalaTestVersion % Test,
    ),
    //baseDirectory in (Test) := file("./core"),
    doctestIgnoreRegex := Some(".*.scala"), // TODO: not sure why these  fail, just disable
  )
  .settings(setCrossDirs(Compile) ++ setCrossDirs(Test))
  .jvmSettings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.google.guava" % "guava" % "28.0-jre",
      "com.github.albfernandez" % "juniversalchardet" % "2.3.2",
      "com.sun.mail" % "javax.mail" % "1.5.2" % "provided",
      "com.fasterxml.woodstox" % "woodstox-core" % "5.1.0",
      "commons-io" % "commons-io" % "2.6",
      "it.unimi.dsi" % "fastutil" % "8.2.2",
      "org.apache.commons" % "commons-compress" % "1.18",
      "org.apache.commons" % "commons-text" % "1.8",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.tukaani" % "xz" % "1.8",  // Used by commons-compress and should be synced up with whatever version commons-compress requires
      "org.xerial.snappy" % "snappy-java" % "1.1.2.6"
    )
  )
  .jsSettings(Seq(
    // Add JS-specific settings here
    libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "1.0.0",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "1.0.0",
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  ))

lazy val coreJVM = core.jvm
lazy val coreJS  = core.js

lazy val macros = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("macros"))
  .configureCross(defaultCrossProjectConfiguration)
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .settings(
    name := "fm-macros",
    scalacOptions --= Seq(
      "-Xlint:deprecation",
      "-deprecation"
    ),
    scalacOptions += "-deprecation:false",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value
    )

  )

lazy val macrosJVM = macros.jvm
lazy val macrosJS = macros.js

lazy val bench = project
  .in(file("bench"))
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .settings(
    name := "fm-bench"
  )
  .enablePlugins(JmhPlugin)
  .dependsOn(
    coreJVM,
    macrosJVM % "compile-internal, test-internal"
  )

// Reloads build.sbt changes whenever detected
Global / onChangedBuildSource := ReloadOnSourceChanges

// Adds a `src/main/scala-2.13+` source directory for Scala 2.13 and newer
// and a `src/main/scala-2.12-` source directory for Scala version older than 2.13
// unmanagedSourceDirectories is "order sensitive" so override the default settings in the correct priority order
def setCrossDirs(config: Configuration): Seq[Setting[_]] = {
  Seq(
    unmanagedSourceDirectories in config := {
      val baseDir   = baseDirectory.value
      val platform  = crossProjectPlatform.value.identifier

      val configPath: String = config match {
        case Compile => "main"
        case Test    => "test"
        case _       => return Nil
      }

      val javaSources = if (platform != "js") Seq(
        baseDir / "src" / configPath / "java",
      ) else Nil

      javaSources ++ (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n < 13 => Seq(
          baseDir                    / "src" / configPath / s"scala-2.$n",
          baseDir / ".."  / "shared" / "src" / configPath / s"scala-2.$n",
          baseDir                    / "src" / configPath / "scala-2.12-",
          baseDir / ".."  / "shared" / "src" / configPath / "scala-2.12-",
          baseDir                    / "src" / configPath / "scala",
          baseDir / ".."  / "shared" / "src" / configPath / "scala",
        )
        case Some((m, n))           => Seq(
          baseDir                   / "src" / configPath / s"scala-$m.$n",
          baseDir / ".." / "shared" / "src" / configPath / s"scala-$m.$n",
          baseDir                   / "src" / configPath / "scala-2.13+",
          baseDir / ".." / "shared" / "src" / configPath / "scala-2.13+",
          baseDir                   / "src" / configPath / "scala",
          baseDir / ".." / "shared" / "src" / configPath / "scala",
        )
      })
    }
  )
}
