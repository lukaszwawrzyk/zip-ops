package org.virtuslab.zipops.bench

import org.virtuslab.zipops.ZipOps
import org.openjdk.jmh.annotations._
import java.io.File
import java.nio.file.{ Files, Path }
import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import ZipOpsBench._
import org.openjdk.jmh.infra.Blackhole
import org.virtuslab.zipops.impl.MyZipFsOps
import sbt.io.IO

class BigJarStamperBenchmark extends StamperBenchmark(BigJar)
class SmallJarStamperBenchmark extends StamperBenchmark(SmallJar)

@State(Scope.Thread)
abstract class StamperBenchmark(jar: String) extends ZipOpsBench with BenchUtil {

  var jarFile: File = _
  var inJarPaths: Seq[String] = _
  var extractDir: Path = _
  var absExtractedPaths: Seq[Path] = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    jarFile = copyResource(jar)
    extractDir = extractSomewhere(jarFile)
    inJarPaths = MyZipFsOps.readPaths(jarFile)
    absExtractedPaths = inJarPaths.map(extractDir.resolve).map(_.toAbsolutePath)
  }

  @TearDown(Level.Trial)
  def teardown(): Unit = {
    jarFile.delete()
    IO.delete(extractDir.toFile)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def files(hole: Blackhole): Unit = {
    absExtractedPaths.foreach(f => hole.consume(Files.getLastModifiedTime(f)))
  }


  override def run(ops: ZipOps): Unit = {
    val stamper = ops.createStamper(jarFile)
    inJarPaths.foreach { path =>
      val stamp = stamper.readStamp(jarFile, path)
      if (stamp == 0) ???
    }
  }
}