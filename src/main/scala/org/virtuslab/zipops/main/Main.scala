package org.virtuslab.zipops.main

import org.virtuslab.zipops.ZipOps
import org.virtuslab.zipops.bench.BenchUtil
import org.virtuslab.zipops.impl.MyZipFsOps

object Main extends BenchUtil {

  private val ops: ZipOps = MyZipFsOps

  def main(args: Array[String]): Unit = {
    delete()
  }

  private def readCentralDir() = {
    val file = copyResource("zip-ops_2.12-0.1.jar")
    ops.readCentralDirectory(file)
    file.delete()
  }

  private def merge() = {
    val target = copyResource("scala-library-2.12.6.jar")
    val source = copyResource("scala-xml_2.12-1.0.6.jar")
    ops.mergeArchives(target, source)
    target.delete()
  }

  private def delete() = {
    val target = copyResource("zip-ops_2.12-0.1.jar")
    println(s"Created $target")
    ops.removeEntries(target, Set("org/virtuslab/zipops/ZipOps.class"))
    Console.readLine(s"Removed org/virtuslab/zipios/ZipOps.class")
    target.delete()
  }
}
