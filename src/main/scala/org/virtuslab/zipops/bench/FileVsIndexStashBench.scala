package org.virtuslab.zipops.bench

import java.io.File
import java.nio.file.{ Files, StandardCopyOption }
import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.openjdk.jmh.annotations._
import org.virtuslab.zipops.bench.ZipOpsBench._
import org.virtuslab.zipops.impl.MyZipFsOps

class BigJarFileVsIndexStashBench extends FileVsIndexStashBench(BigJar)

@State(Scope.Thread)
abstract class FileVsIndexStashBench(jar: String) extends BenchUtil {

  var jarFile: File = _

  @Setup(Level.Invocation)
  def setup(): Unit = {
    jarFile = copyResource(jar)
  }

  @TearDown(Level.Invocation)
  def teardown(): Unit = {
    jarFile.delete()
  }


  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def stashFile(): Unit = {
    val jarPath = jarFile.toPath
    val backupPath = jarPath.resolveSibling(s"${jarPath.getFileName}.bak")
    Files.copy(jarPath, backupPath)
    Files.move(backupPath, jarPath, StandardCopyOption.REPLACE_EXISTING)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def stashIndex(): Unit = {
    val metadata = MyZipFsOps.stashIndex(jarFile.toPath)
    MyZipFsOps.unstashIndex(metadata, jarFile.toPath)
  }

}
