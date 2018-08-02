package org.virtuslab.zipops

import java.nio.file.{ Files, StandardCopyOption }

object Main {

  val toDelete = Set(
    "scala/util/matching/Regex$MatchIterator$$anon$1.class",
    "scala/sys/process/ProcessIO.class",
    "scala/runtime/VolatileShortRef.class",
    "scala/math/Equiv.class",
    "scala/Array$.class",
    "scala/Array.class",
    "scala/Boolean$.class",
    "scala/Boolean.class",
    "scala/Byte$.class",
    "scala/Byte.class",
    "scala/Char$.class",
    "scala/Char.class",
    "scala/Cloneable.class",
    "scala/Console$.class",
    "scala/Console.class",
    "scala/DelayedInit.class",
    "scala/DeprecatedConsole.class",
    "scala/DeprecatedPredef.class",
    "scala/Double$.class",
    "scala/Double.class",
    "scala/Dynamic.class"
  )

  def main(args: Array[String]): Unit = {
    val testJar = getClass.getResourceAsStream("/scala-library-2.12.6.jar")
    val tempFile = Files.createTempFile("del_bench", ".jar")
    Files.copy(testJar, tempFile, StandardCopyOption.REPLACE_EXISTING)
    val jarFile = tempFile.toFile
    Zip4jZipOps.removeEntries(jarFile, toDelete)
    jarFile.delete()
  }

}
