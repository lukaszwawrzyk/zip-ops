package org.virtuslab.zipops.bench

import java.io.File

import org.virtuslab.zipops.{ Zip4jZipOps, ZipFsZipOps }
import java.util.concurrent.TimeUnit.SECONDS

import org.openjdk.jmh.annotations._

class MergeBenchSmall extends MergeBench("scala-xml_2.12-1.0.6.jar", "zip-ops_2.12-0.1.jar")

class MergeBenchBig extends MergeBench("scala-library-2.12.6.jar", "scala-xml_2.12-1.0.6.jar")

@State(Scope.Thread)
abstract class MergeBench(target: String, source: String) extends BenchUtil {

  var targetFile: File = _
  var sourceFile: File = _

  @Setup(Level.Invocation)
  def setup(): Unit = {
    targetFile = copyResource(target)
    sourceFile = copyResource(source)
  }

  @TearDown(Level.Invocation)
  def teardown(): Unit = {
    targetFile.delete()
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
  @Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zip4jMerge(): Unit = {
    Zip4jZipOps.mergeArchives(targetFile, sourceFile)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
  @Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zipfsMerge(): Unit = {
    ZipFsZipOps.mergeArchives(targetFile, sourceFile)
  }
}
