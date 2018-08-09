package org.virtuslab.zipops.bench

import org.virtuslab.zipops.ZipOps
import org.openjdk.jmh.annotations._
import java.io.File

//class BigCentralDirBench extends CentralDirBenchmark("scala-library-2.12.6.jar")

@State(Scope.Thread)
abstract class CentralDirBenchmark(jar: String) extends ZipOpsBench with BenchUtil {

  var jarFile: File = _

  @Setup(Level.Invocation)
  def setup(): Unit = {
    jarFile = copyResource(jar)
  }

  @TearDown(Level.Invocation)
  def teardown(): Unit = {
    jarFile.delete()
  }

  override def run(ops: ZipOps): Unit = ops.readCentralDirectory(jarFile)
}