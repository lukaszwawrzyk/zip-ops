package org.virtuslab.zipops.bench

import org.virtuslab.zipops.{ SimpleZipFsOps, Zip4jOps, ZipOps }
import org.openjdk.jmh.annotations._
import java.io.File
import java.nio.file.{ Files, Path }
import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import net.lingala.zip4j.core.ZipFile
import org.openjdk.jmh.infra.Blackhole

class BigJarStamperBenchmark extends StamperBenchmark("scala-library-2.12.6.jar")

@State(Scope.Thread)
abstract class StamperBenchmark(jar: String) extends ZipOpsBench with BenchUtil {

  var jarFile: File = _
  var extractDir: Path = _
  var paths: Seq[String] = _
  var absPaths: Seq[Path] = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    jarFile = copyResource(jar)
    extractDir = Files.createTempDirectory("lols")
    new ZipFile(jarFile).extractAll(extractDir.toString)
    paths = SimpleZipFsOps.readPaths(jarFile)
    absPaths = paths.map(extractDir.resolve).map(_.toAbsolutePath)
  }

  @TearDown(Level.Trial)
  def teardown(): Unit = {
    jarFile.delete()
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 2, time = 5, timeUnit = SECONDS)
  @Measurement(iterations = 5, time = 20, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def files(hole: Blackhole): Unit = {
    absPaths.foreach(f => hole.consume(Files.getLastModifiedTime(f)))
  }


  override def run(ops: ZipOps): Unit = {
    val stamper = ops.createStamper(jarFile)
    paths.foreach { path =>
      val stamp = stamper.readStamp(jarFile, path)
      if (stamp == 0) ???
    }
  }
}