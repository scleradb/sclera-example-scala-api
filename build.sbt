name := "sclera-example-scala-api"

description := "Sclera JDBC Example and Template - Scala version"

version := "4.0-SNAPSHOT"

homepage := Some(url("https://github.com/scleradb/sclera-example-scala-api"))

scalaVersion := "2.13.1"

licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT"))

libraryDependencies ++= Seq(
    "com.scleradb" %% "sclera-config" % "4.0",
    "com.scleradb" %% "sclera-core" % "4.0"
)

scalacOptions ++= Seq(
    "-Werror", "-feature", "-deprecation", "-unchecked"
)

val mkscript = taskKey[File]("Create executable script")

mkscript := {
    val cp = (fullClasspath in Test).value
    val mainClassStr = (mainClass in Runtime).value getOrElse sys.error(
        "main class not found"
    )

    val (template, scriptName) =
        if( System.getProperty("os.name").startsWith("Windows") ) (
            """|@ECHO OFF
               |java -Xmx512m -classpath "%%CLASSPATH%%:%s" %s %%*
               |""".stripMargin,
            "scleraexample.cmd"
        ) else (
            """|#!/bin/sh
               |java -Xmx512m -classpath "$CLASSPATH:%s" %s $@
               |""".stripMargin,
            "scleraexample"
        )

    val contents = template.format(cp.files.absString, mainClassStr)
    val out = baseDirectory.value / "bin" / scriptName

    IO.write(out, contents)
    out.setExecutable(true)

    out
}
