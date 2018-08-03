package org.virtuslab.zipops.bench

import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.virtuslab.zipops.{ Zip4jZipOps, ZipFsZipOps, ZipOps }
import org.openjdk.jmh.annotations._

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 5, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 10, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zip4j(): Unit = {
    run(Zip4jZipOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 5, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 10, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfs(): Unit = {
    run(ZipFsZipOps)
  }

}
