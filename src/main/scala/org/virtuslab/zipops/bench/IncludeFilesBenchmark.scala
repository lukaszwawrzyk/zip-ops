package org.virtuslab.zipops.bench

import org.virtuslab.zipops.ZipOps
import java.io.File

import ZipOpsBench._

import org.openjdk.jmh.annotations._
import sbt.io.{ IO, DirectoryFilter }

class IncludeSomeFilesBench extends IncludeFilesBenchmark(zip = BigJar, zipToExtractAndInclude = SmallJar)

@State(Scope.Thread)
abstract class IncludeFilesBenchmark(
  zip: String,
  zipToExtractAndInclude: String
) extends ZipOpsBench with BenchUtil {

  var zipFile: File = _
  var extractDir: File = _
  var extractedFiles: Seq[(File, String)] = _

  @Setup(Level.Invocation)
  def setup(): Unit = {
    zipFile = copyResource(zip)

    val zipToExtract = copyResource(zipToExtractAndInclude)
    extractDir = extractSomewhere(zipToExtract).toFile
    extractedFiles = dirContent(extractDir)
  }

  private def dirContent(dir: File): Seq[(File, String)] = {
    import sbt.io.syntax._
    (dir ** -DirectoryFilter).get.flatMap { extractedFile =>
      IO.relativize(dir, extractedFile) match {
        case Some(relPath) =>
          List((extractedFile, relPath))
        case _ => Nil
      }
    }
  }

  @TearDown(Level.Invocation)
  def teardown(): Unit = {
    zipFile.delete()
    IO.delete(extractDir)
  }

  override def run(ops: ZipOps): Unit = ops.includeFiles(zipFile, extractedFiles)

}
