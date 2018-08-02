package org.virtuslab.zipops

import java.io.File

trait ZipOps {
  def removeEntries(jarFile: File, classes: Iterable[String]): Unit
  def mergeArchives(into: File, from: File): Unit
}
