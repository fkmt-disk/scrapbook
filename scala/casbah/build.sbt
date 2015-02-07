scalaVersion := "2.11.5"

libraryDependencies += "org.mongodb" % "casbah_2.11" % "2.8.0"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

fork := true

unmanagedClasspath in Runtime <+= (baseDirectory) map { dir => Attributed.blank(dir) }
