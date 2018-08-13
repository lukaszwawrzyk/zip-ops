package org.virtuslab.zipops.bench

import org.virtuslab.zipops.ZipOps
import java.io.File
import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import org.virtuslab.zipops.ZipOps.InZipPath
import org.virtuslab.zipops.bench.ZipOpsBench._
import org.virtuslab.zipops.impl.MyZipFsOps

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
  }

  override def run(ops: ZipOps): Unit = {
    val allPaths =ops.readPaths(jarFile)
    paths.foreach(allPaths.contains)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def files(hole: Blackhole): Unit = {
    extractedFiles.foreach(_.exists())
  }

}