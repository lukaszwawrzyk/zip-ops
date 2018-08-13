package org.virtuslab.zipops.bench

import java.io.File
import java.nio.file.{ Files, StandardCopyOption, Path }

import net.lingala.zip4j.core.ZipFile
import sbt.io.{ IO, DirectoryFilter }

trait BenchUtil {

  def dirContent(dir: File): Seq[(File, String)] = {
    import sbt.io.syntax._
    (dir ** -DirectoryFilter).get.flatMap { extractedFile =>
      IO.relativize(dir, extractedFile) match {
        case Some(relPath) =>
          List((extractedFile, relPath))
        case _ => Nil
      }
    }
  }

  def copyResource(name: String): File = {
    val resource = getClass.getResourceAsStream(s"/$name")
    val tempFile = Files.createTempFile("bench", ".jar")
    Files.copy(resource, tempFile, StandardCopyOption.REPLACE_EXISTING)
    tempFile.toFile
  }

  def extractSomewhere(file: File): Path = {
    val tempDir = Files.createTempDirectory("lols")
    extract(file, tempDir)
    tempDir
  }

  private def extract(zip: File, targetDir: Path): Unit = {
    new ZipFile(zip).extractAll(targetDir.toString)
  }


}
