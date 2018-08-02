package org.virtuslab.zipops.bench

import java.io.File
import java.nio.file.{ Files, StandardCopyOption }
import java.util.concurrent.TimeUnit.SECONDS

import org.openjdk.jmh.annotations._
import org.virtuslab.zipops.{ Zip4jZipOps, ZipFsZipOps, ZipOps }

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 10, time = 10, timeUnit = SECONDS)
  @Measurement(iterations = 15, time = 15, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zip4j(): Unit = {
    run(Zip4jZipOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 10, time = 10, timeUnit = SECONDS)
  @Measurement(iterations = 15, time = 15, timeUnit = SECONDS)
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
