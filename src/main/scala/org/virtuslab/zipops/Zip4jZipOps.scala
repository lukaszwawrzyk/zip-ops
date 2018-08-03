package org.virtuslab.zipops

import java.io.{ RandomAccessFile, File, OutputStream }

import net.lingala.zip4j.core.{ HeaderReader, HeaderWriter, EfficientHeaderReader }
import java.nio.channels.{ FileChannel, Channels }
import java.nio.file.{ Files, Path }
import java.util.ArrayList

import scala.collection.JavaConverters._
import net.lingala.zip4j.model.{ ZipModel, FileHeader }
import org.virtuslab.zipops.bench.AbstractZipOps

object Zip4jZipOps extends Zip4jZipOpsBase {

  override protected def readMetadata(path: Path): Metadata = {
    val headerReader = new EfficientHeaderReader(path)
    val centralDir = headerReader.readAllHeaders()
    headerReader.close()
    centralDir
  }


}

object Zip4jZipOpsBaseline extends Zip4jZipOpsBase

trait Zip4jZipOpsBase extends AbstractZipOps {

  override type Metadata = ZipModel
  override type Header = FileHeader

  protected def readMetadata(path: Path): Metadata = {
    val file = new RandomAccessFile(path.toFile, "r")
    val headerReader = new HeaderReader(file)
    val centralDir = headerReader.readAllHeaders()
    file.close()
    centralDir
  }

  protected def getCentralDirStart(metadata: Metadata): Long = {
    metadata.getEndCentralDirRecord.getOffsetOfStartOfCentralDir
  }
  protected def setCentralDirStart(metadata: Metadata, centralDirStart: Long): Unit = {
    metadata.getEndCentralDirRecord.setOffsetOfStartOfCentralDir(centralDirStart)
  }

  protected def getHeaders(metadata: Metadata): Seq[Header] = {
    metadata.getCentralDirectory.getFileHeaders.asInstanceOf[ArrayList[FileHeader]].asScala
  }
  protected def setHeaders(metadata: Metadata, headers: Seq[Header]): Unit = {
    metadata.getCentralDirectory.setFileHeaders(asArrayList(headers))
  }

  protected def getFileName(header: Header): String = header.getFileName

  protected def setFileOffset(header: Header): Long = header.getOffsetLocalHeader
  protected def getFileOffset(header: Header, offset: Long): Unit = header.setOffsetLocalHeader(offset)

  protected def dumpMetadata(metadata: Metadata, outputStream: OutputStream): Unit = {
    val headerWriter = new HeaderWriter
    headerWriter.finalizeZipFile(metadata, outputStream)
  }
  protected def asArrayList[A](clearedHeaders: Seq[A]): ArrayList[A] = {
    new ArrayList[A](clearedHeaders.asJava)
  }

}
