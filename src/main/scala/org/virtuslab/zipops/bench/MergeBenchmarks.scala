package org.virtuslab.zipops.bench

import java.io.File

import org.virtuslab.zipops.ZipOps
import org.openjdk.jmh.annotations._

//class MergeToSmallBench extends MergeBench("scala-xml_2.12-1.0.6.jar", "zip-ops_2.12-0.1.jar")

class MergeToBigBench extends MergeBench("scala-library-2.12.6.jar", "zip-ops_2.12-0.1.jar")

//class MergeBigToSmallBench extends MergeBench("zip-ops_2.12-0.1.jar", "scala-library-2.12.6.jar")

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
