package org.virtuslab.zipops.bench

import java.io.File
import java.util.concurrent.TimeUnit.SECONDS

import org.openjdk.jmh.annotations._
import org.virtuslab.zipops.{ Zip4jZipOps, ZipFsZipOps }

class BigJarDeleteBench extends DeleteBenchmark("scala-library-2.12.6.jar") {

  val toDelete = Set(
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

}

class SmallJarDeleteBench extends DeleteBenchmark("scala-xml_2.12-1.0.6.jar") {

  val toDelete = Set(
    "scala/xml/pull/EvText.class",
    "scala/xml/dtd/ExtDef.class",
    "scala/xml/Atom.class"
  )

}

@State(Scope.Thread)
abstract class DeleteBenchmark(jar: String) extends BenchUtil {

  var jarFile: File = _

  val toDelete: Set[String]

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
  @Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
  @Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zip4jRemove(): Unit = {
    Zip4jZipOps.removeEntries(jarFile, toDelete)
  }

  @Benchmark
  @Fork(value = 1)
  @Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
  @Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
  @BenchmarkMode(Array(Mode.Throughput))
  @OutputTimeUnit(SECONDS)
  def zipfsRemove(): Unit = {
    ZipFsZipOps.removeEntries(jarFile, toDelete)
  }
}
