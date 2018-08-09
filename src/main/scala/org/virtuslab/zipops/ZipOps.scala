package org.virtuslab.zipops

import java.io.File

trait ZipOps {
  def readPaths(jar: File): Seq[String]
  def createStamper(jar: File): Stamper
  def readCentralDirectory(jar: File): Unit = ()
  def removeEntries(jarFile: File, classes: Iterable[String]): Unit
  def mergeArchives(into: File, from: File): Unit
}

trait Stamper {
  def readStamp(jar: File, cls: String): Long
}
