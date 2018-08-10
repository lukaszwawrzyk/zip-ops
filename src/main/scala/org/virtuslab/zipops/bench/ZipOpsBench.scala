package org.virtuslab.zipops.bench

import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.virtuslab.zipops._
import org.openjdk.jmh.annotations._
import ZipOpsBench._
import org.virtuslab.zipops.impl.{ ZipFsOps, Zip4jOps, MyZipFsOps }

object ZipOpsBench {
  final val WarmupIterations = 2
  final val WarmupIterationTime = 20
  final val Iterations = 10
  final val IterationTime = 20
}

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zip4j(): Unit = {
    run(Zip4jOps)
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
