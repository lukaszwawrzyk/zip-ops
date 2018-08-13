package org.virtuslab.zipops.bench

import org.virtuslab.zipops.ZipOps
import java.io.File
import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }
import java.util.zip.ZipFile

import org.openjdk.jmh.annotations._
import org.virtuslab.zipops.ZipOps.InZipPath
import org.virtuslab.zipops.bench.ZipOpsBench._
import org.virtuslab.zipops.impl.MyZipFsOps
import sbt.io.IO

class BigJarExistenceTestBench extends ExistenceCheckBenchmark(BigJar)
class SmallJarExistenceTestBench extends ExistenceCheckBenchmark(SmallJar)

@State(Scope.Thread)
abstract class ExistenceCheckBenchmark(jar: String) extends ZipOpsBench with BenchUtil {

  var jarFile: File = _
  var extractDir: File = _
  var paths: Seq[InZipPath] = _
  var extractedFiles: Seq[File] = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    jarFile = copyResource(jar)
    paths = MyZipFsOps.readPaths(jarFile)

    extractDir = extractSomewhere(jarFile).toFile
    extractedFiles = dirContent(extractDir).map(_._1)
  }

  @TearDown(Level.Trial)
  def teardown(): Unit = {
    jarFile.delete()
    IO.delete(extractDir)
  }

  override def run(ops: ZipOps): Unit = {
    val allPaths = ops.readPaths(jarFile).toSet
    paths.foreach(allPaths.contains)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def files: Unit = {
    extractedFiles.foreach(_.exists())
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfile: Unit = {
    val allPaths = {
      import scala.collection.JavaConverters._
      val zip = new ZipFile(jarFile)
      val names = zip.entries().asScala.filterNot(_.isDirectory).map(_.getName).toSet
      zip.close()
      names
    }
    paths.foreach(allPaths.contains)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfileReopen: Unit = {
    val allPaths = {
      import scala.collection.JavaConverters._
      val zip = new ZipFile(jarFile)
      val names = zip.entries().asScala.filterNot(_.isDirectory).map(_.getName).toSet
      zip.close()
      names
    }
    paths.foreach { path =>
      val zip = new ZipFile(jarFile)
      zip.getEntry(path)
      zip.close()
    }
  }


}