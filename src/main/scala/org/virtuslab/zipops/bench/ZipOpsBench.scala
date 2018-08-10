package org.virtuslab.zipops.bench

import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.virtuslab.zipops._
import org.openjdk.jmh.annotations._
import ZipOpsBench._
import org.virtuslab.zipops.impl.{ MyZipFsOps, ZipFsOps, Zip4jOps, MyZipOpsZipDifferently }

object ZipOpsBench {
  final val WarmupIterations = 2
  final val WarmupIterationTime = 5
  final val Iterations = 8
  final val IterationTime = 5

  val BigJar = "scala-library-2.12.6.jar" // 2542 files
  val MediumJar = "scala-xml_2.12-1.0.6.jar" // 249 files
  val SmallJar = "zip-ops_2.12-0.1.jar" // 9 files, 3 dirs
}

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def myzipfs2(): Unit = {
    run(MyZipOpsZipDifferently)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfs(): Unit = {
    run(ZipFsOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def myZipfs(): Unit = {
    run(MyZipFsOps)
  }

}
