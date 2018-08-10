package org.virtuslab.zipops

import java.io.File

import org.virtuslab.zipops.ZipOps.{ InZipPath, Timestamp }

object ZipOps {
  type InZipPath = String
  type Timestamp = Long
}

trait ZipOps {
  def readPaths(jar: File): Seq[InZipPath]
  def createStamper(jar: File): Stamper
  def readCentralDirectory(jar: File): Unit = ()
  def removeEntries(jarFile: File, classes: Iterable[InZipPath]): Unit
  def mergeArchives(into: File, from: File): Unit
  def includeFiles(zip: File, files: Seq[(File, InZipPath)])
}

trait Stamper {

  def readStamp(jar: File, cls: InZipPath): Timestamp
}
