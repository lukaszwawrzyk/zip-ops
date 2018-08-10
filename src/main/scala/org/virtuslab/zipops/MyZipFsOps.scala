package org.virtuslab.zipops

import java.io.OutputStream
import java.nio.file.Path

import scala.collection.JavaConverters._

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
    header.mtime
  }

  protected def dumpMetadata(metadata: Metadata, outputStream: OutputStream): Unit = {
    metadata.dump(outputStream)
  }
}
