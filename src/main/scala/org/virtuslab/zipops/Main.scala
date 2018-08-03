package org.virtuslab.zipops

import org.virtuslab.zipops.bench.BenchUtil

object Main extends BenchUtil {

  def main(args: Array[String]): Unit = {
    val file = copyResource("zip-ops_2.12-0.1.jar")
    Zip4jZipOps.readCentralDirectory(file)
    file.delete()
  }

  private def merge() = {
    val target = copyResource("scala-library-2.12.6.jar")
    val source = copyResource("scala-xml_2.12-1.0.6.jar")
    Zip4jZipOps.mergeArchives(target, source)
    target.delete()
  }
}
