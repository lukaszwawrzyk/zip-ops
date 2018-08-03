package org.virtuslab.zipops.bench

import java.io.File
import java.nio.file.{ Files, StandardCopyOption }
import java.util.concurrent.TimeUnit.SECONDS

import org.openjdk.jmh.annotations._
import org.virtuslab.zipops._

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 5, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 10, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zip4j(): Unit = {
    run(Zip4jZipOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 5, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 10, time = 5, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zipfs(): Unit = {
    run(ZipFsZipOps)
  }

}

trait BenchUtil {

  def copyResource(name: String): File = {
    val resource = getClass.getResourceAsStream(s"/$name")
    val tempFile = Files.createTempFile("del_bench", ".jar")
    Files.copy(resource, tempFile, StandardCopyOption.REPLACE_EXISTING)
    tempFile.toFile
  }

}
