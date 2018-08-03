package org.virtuslab.zipops.bench

import java.io.File
import java.nio.file.{ Files, StandardCopyOption }

trait BenchUtil {

  def copyResource(name: String): File = {
    val resource = getClass.getResourceAsStream(s"/$name")
    val tempFile = Files.createTempFile("bench", ".jar")
    Files.copy(resource, tempFile, StandardCopyOption.REPLACE_EXISTING)
    tempFile.toFile
  }

}
