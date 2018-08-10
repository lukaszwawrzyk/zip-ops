package org.virtuslab.zipops.impl

import java.io.{ File, OutputStream }
import java.nio.file.Path
import java.util.UUID

import org.virtuslab.zipops.ZipOps.InZipPath
import org.virtuslab.zipops.{ ZipMetadata, CreateZip, IndexBasedZipOps }

import scala.collection.JavaConverters._

object MyZipOpsZipDifferently extends MyZipFsOpsBase with WithZipFs {

  override def includeFiles(zip: File, files: Seq[(File, InZipPath)]): Unit = {
    val tempZip = zip.toPath.resolveSibling(UUID.randomUUID().toString + ".jar").toFile
    CreateZip.usingOptimizedSbtIo(tempZip, files)
    mergeArchives(zip, tempZip)
  }

}

object MyZipFsOps extends MyZipFsOpsBase

trait MyZipFsOpsBase extends IndexBasedZipOps {
  override type Metadata = ZipMetadata
  override type Header = ZipMetadata.Entry

  protected def readMetadata(path: Path): Metadata = {
    new ZipMetadata(path)
  }

  protected def getCentralDirStart(metadata: Metadata): Long = {
    metadata.getCentralDirStart
  }

  protected def setCentralDirStart(metadata: Metadata, centralDirStart: Long): Unit = {
    metadata.setCentralDirStart(centralDirStart)
  }

  protected def getHeaders(metadata: Metadata): Seq[Header] = {
    metadata.getHeaders.asScala
  }
  protected def setHeaders(metadata: Metadata, headers: Seq[Header]): Unit = {
    metadata.setHeaders(new java.util.ArrayList[Header](headers.asJava))
  }

  protected def getFileName(header: Header): String = {
    header.getName
  }

  protected def getFileOffset(header: Header): Long = {
    header.getEntryOffset
  }

  protected def setFileOffset(header: Header, offset: Long): Unit = {
    header.setEntryOffset(offset)
  }

  protected def getLastModifiedTime(header: Header): Long = {
    header.getLastModifiedTime
  }

  protected def dumpMetadata(metadata: Metadata, outputStream: OutputStream): Unit = {
    metadata.dump(outputStream)
  }
}