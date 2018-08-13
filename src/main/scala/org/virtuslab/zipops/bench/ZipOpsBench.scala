package org.virtuslab.zipops.bench

import java.util.concurrent.TimeUnit.{ SECONDS, MILLISECONDS }

import org.virtuslab.zipops._
import org.openjdk.jmh.annotations._
import org.virtuslab.zipops.bench.ZipOpsBench._
import org.virtuslab.zipops.impl._

object ZipOpsBench {
  final val WarmupIterations = 2
  final val WarmupIterationTime = 5
  final val Iterations = 15
  final val IterationTime = 5

  val BigJar = "scala-library-2.12.6.jar" // 2542 files
  val MediumJar = "scala-xml_2.12-1.0.6.jar" // 249 files
  val SmallJar = "zip-ops_2.12-0.1.jar" // 9 files, 3 dirs

  val FilesInBigJar = Set(
    "scala/util/matching/Regex$MatchIterator$$anon$1.class",
    "scala/sys/process/ProcessIO.class",
    "scala/runtime/VolatileShortRef.class",
    "scala/math/Equiv.class",
    "scala/Array$.class",
    "scala/Array.class",
    "scala/Boolean$.class",
    "scala/Boolean.class",
    "scala/Byte$.class",
    "scala/Byte.class",
    "scala/Char$.class",
    "scala/Char.class",
    "scala/Cloneable.class",
    "scala/Console$.class",
    "scala/Console.class",
    "scala/DelayedInit.class",
    "scala/DeprecatedConsole.class",
    "scala/DeprecatedPredef.class",
    "scala/Double$.class",
    "scala/Double.class",
    "scala/Dynamic.class"
  )

  val FilesInMediumJar = Set(
    "scala/xml/pull/EvText.class",
    "scala/xml/dtd/ExtDef.class",
    "scala/xml/Atom.class"
  )

}

trait ZipOpsBench {

  def run(ops: ZipOps): Unit

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zip4j(): Unit = {
    run(Zip4jOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def zipfs(): Unit = {
    run(ZipFsOps)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = WarmupIterations, time = WarmupIterationTime, timeUnit = SECONDS)
  @Measurement(iterations = Iterations, time = IterationTime, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(MILLISECONDS)
  def myZipfs(): Unit = {
    run(MyZipFsOps)
  }

}
