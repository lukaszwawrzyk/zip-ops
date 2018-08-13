package org.virtuslab.zipops.bench

import org.virtuslab.zipops.ZipOps
import org.openjdk.jmh.annotations._
import java.io.File
import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }
import java.util.zip.ZipFile

import ZipOpsBench._

class BigCentralDirBench extends CentralDirBenchmark(BigJar)

@State(Scope.Thread)
abstract class CentralDirBenchmark(jar: String) extends ZipOpsBench with BenchUtil {

  var jarFile: File = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    jarFile = copyResource(jar)
  }

  @TearDown(Level.Trial)
  def teardown(): Unit = {
    jarFile.delete()
  }

  override def run(ops: ZipOps): Unit = ops.readCentralDirectory(jarFile)

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfile(): Unit = {
    import scala.collection.JavaConverters._
    val zip = new ZipFile(jarFile)
    val names = zip.entries().asScala.filterNot(_.isDirectory).map(_.getName)
    zip.close()
  }

}