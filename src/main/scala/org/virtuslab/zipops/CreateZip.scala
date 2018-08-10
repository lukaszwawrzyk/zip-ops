package org.virtuslab.zipops

import org.virtuslab.zipops.ZipOps.InZipPath
import java.nio.file.{ Files, StandardCopyOption }
import java.io.File
import java.util.zip.{ ZipOutputStream, ZipEntry, Deflater }

import org.virtuslab.zipops.impl.WithZipFs
import sbt.io.{ IO, Using }

object CreateZip extends WithZipFs {

  def usingZipFs(target: File, files: Seq[(File, InZipPath)]): Unit = {
    withZipFs(target, create = true) { fs =>
      files.foreach { case (file, target) =>
        val targetPath = fs.getPath(target)
        Option(targetPath.getParent).foreach(Files.createDirectories(_))
        Files.copy(file.toPath, targetPath, StandardCopyOption.COPY_ATTRIBUTES)
      }
    }
  }

  def usingSbtIo(target: File, files: Seq[(File, InZipPath)]): Unit = {
    IO.zip(files, target)
  }

  def usingOptimizedSbtIo(target: File, files: Seq[(File, InZipPath)]): Unit = {
    withZipOutput(target) { output =>
      writeZip(files, output)
    }
  }

  private def withZipOutput(file: File)(f: ZipOutputStream => Unit): Unit = {
    Using.fileOutputStream(false)(file) { fileOut =>
      val zipOut = new ZipOutputStream(fileOut)
      zipOut.setMethod(ZipOutputStream.DEFLATED)
      zipOut.setLevel(Deflater.NO_COMPRESSION)
      try { f(zipOut) } finally { zipOut.close() }
    }
  }

  private def writeZip(files: Seq[(File, String)], output: ZipOutputStream): Unit = {
    def makeFileEntry(file: File, name: String): ZipEntry = {
      val e = new ZipEntry(name)
      e.setTime(IO.getModifiedTimeOrZero(file))
      e
    }

    def addFileEntry(file: File, name: String): Unit = {
      output.putNextEntry(makeFileEntry(file, name))
      IO.transfer(file, output)
      output.closeEntry()
    }

    files.foreach { case (file, name) => addFileEntry(file, normalizeName(name)) }
  }

  private def normalizeName(name: String): String = {
    val sep = File.separatorChar
    if (sep == '/') name else name.replace(sep, '/')
  }

}
