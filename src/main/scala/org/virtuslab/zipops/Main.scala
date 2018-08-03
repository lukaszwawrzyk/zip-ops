package org.virtuslab.zipops

import org.virtuslab.zipops.bench.BenchUtil

object Main extends BenchUtil {

  private val ops: ZipOps = SimpleZipFsOps

  def main(args: Array[String]): Unit = {
    merge()
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
}
