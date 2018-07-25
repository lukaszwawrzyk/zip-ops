package com.virtuslab.zipops

import net.lingala.zip4j.model.FileHeader
import java.nio.file._

object Main extends App {

  def printHeader(h: FileHeader): Unit = println {
    s"""Entry(
       | name = ${h.getFileName}${if (h.isDirectory) " (dir)" else ""}
       | compressedSize = ${h.getCompressedSize}
       | localHeaderOffset = ${h.getOffsetLocalHeader}
       |)""".stripMargin
  }

  def getPath(file: String, copy: Boolean = false): Path = {
    if (copy) {
      val copiedFile = file + ".cpy"
      Files.copy(Paths.get(file), Paths.get(copiedFile), StandardCopyOption.REPLACE_EXISTING)
    } else Paths.get(file)
  }

  def printCentralDir(path: Path): Unit = {
    ZipOps.getCentralDir(path).foreach(printHeader)
  }

//  val path = getPath("/home/lukasz/dev/test/test.jar", copy = true)
//  printCentralDir(path)
//  ZipOps.removeEntries(path, Set("test/A1", "test/B2"))
//  println("AFTER")
//  printCentralDir(path)

  val to = getPath("./test/1.zip", copy = true)
  val from = getPath("./test/2.zip", copy = false)
  println("TO")
  printCentralDir(to)
  println("FROM")
  printCentralDir(from)
  ZipOps.mergeArchives(to, from)
  println("AFTER")
  printCentralDir(to)
}
