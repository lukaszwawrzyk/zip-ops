package org.virtuslab.zipops.bench

import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.virtuslab.zipops._
import org.openjdk.jmh.annotations._

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 4, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 6, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zip4j(): Unit = {
    run(Zip4jOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 4, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 6, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfs(): Unit = {
    run(ZipFsOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 4, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 6, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def myZipfs(): Unit = {
    run(SimpleZipFsOps)
  }

}
