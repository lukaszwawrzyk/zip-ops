package org.virtuslab.zipops.bench

import java.io.File

import org.openjdk.jmh.annotations._
import org.virtuslab.zipops.ZipOps
import ZipOpsBench._

class DeleteFromBigJarBench extends DeleteBenchmark(BigJar, FilesInBigJar)

class SmallJarDeleteBench extends DeleteBenchmark(MediumJar, FilesInMediumJar)

@State(Scope.Thread)
abstract class DeleteBenchmark(jar: String, toDelete: Set[String]) extends ZipOpsBench with BenchUtil {

  var jarFile: File = _

  @Setup(Level.Invocation)
  def setup(): Unit = {
    jarFile = copyResource(jar)
  }

  @TearDown(Level.Invocation)
  def teardown(): Unit = {
    jarFile.delete()
  }

  override def run(ops: ZipOps): Unit = ops.removeEntries(jarFile, toDelete)
}
