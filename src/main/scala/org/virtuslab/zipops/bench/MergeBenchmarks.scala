package org.virtuslab.zipops.bench

import java.io.File

import org.virtuslab.zipops.ZipOps
import org.openjdk.jmh.annotations._
import ZipOpsBench._

class MergeToSmallBench extends MergeBench(MediumJar, SmallJar)

class MergeToBigBench extends MergeBench(BigJar, SmallJar)

class MergeBigToSmallBench extends MergeBench(SmallJar, BigJar)

@State(Scope.Thread)
abstract class MergeBench(target: String, source: String) extends ZipOpsBench with BenchUtil {

  var targetFile: File = _
  var sourceFile: File = _

  @Setup(Level.Invocation)
  def setup(): Unit = {
    targetFile = copyResource(target)
    sourceFile = copyResource(source)
  }

  @TearDown(Level.Invocation)
  def teardown(): Unit = {
    targetFile.delete()
  }

  override def run(ops: ZipOps): Unit = ops.mergeArchives(targetFile, sourceFile)
}
