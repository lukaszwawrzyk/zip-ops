package org.virtuslab.zipops.bench

import java.io.{ RandomAccessFile, File, OutputStream }
import java.nio.channels.{ FileChannel, Channels }
import java.nio.file.{ Files, Path }

import org.virtuslab.zipops.ZipOps

trait AbstractZipOps extends ZipOps {

  override def readCentralDirectory(jar: File): Unit = {
    readMetadata(jar.toPath)
  }

  override def removeEntries(jarFile: File, classes: Iterable[String]): Unit = {
    removeEntries(jarFile.toPath, classes.toSet)
  }

  override def mergeArchives(into: File, from: File): Unit = {
    mergeArchives(into.toPath, from.toPath)
  }

  type Metadata
  type Header

  protected def removeEntries(path: Path, toRemove: Set[String]): Unit = {
    val metadata = readMetadata(path)
    removeEntriesFromMetadata(metadata, toRemove)
    val file = openFile(path)
    truncateMetadata(metadata, file)
    val writeOffset = getCentralDirStart(metadata)
    finalizeZip(metadata, file, writeOffset)
  }

  protected def removeEntriesFromMetadata(metadata: Metadata, toRemove: Set[String]): Unit = {
    val headers = getHeaders(metadata)
    val sanitizedToRemove = toRemove.map(_.stripPrefix("/"))
    val clearedHeaders = headers.filterNot(header => sanitizedToRemove.contains(getFileName(header)))
    setHeaders(metadata, clearedHeaders)
  }

  protected def mergeArchives(target: Path, source: Path): Unit = {
    val targetMetadata = readMetadata(target)
    val sourceMetadata = readMetadata(source)

    val targetFile = openFile(target)
    val sourceFile = openFile(source)

    truncateMetadata(targetMetadata, targetFile)

    // "source" starts where "target" ends
    val sourceStart = targetFile.size()
    // "source" is as long as from its beginning till the start of central dir
    val sourceLength = getCentralDirStart(sourceMetadata)

    transferAll(from = sourceFile, to = targetFile, startPos = sourceStart, bytesToTransfer = sourceLength)
    sourceFile.close()

    val mergedHeaders = mergeHeaders(targetMetadata, sourceMetadata, sourceStart)
    setHeaders(targetMetadata, mergedHeaders)

    val centralDirStart = sourceStart + sourceLength
    setCentralDirStart(targetMetadata, centralDirStart)

    finalizeZip(targetMetadata, targetFile, centralDirStart)

    Files.delete(source)
  }

  protected def mergeHeaders(
    targetModel: Metadata,
    sourceModel: Metadata,
    sourceStart: Long
  ): Seq[Header] = {
    val sourceHeaders = getHeaders(sourceModel)
    sourceHeaders.foreach { header =>
      // potentially offsets should be updated for each header
      // not only in central directory but a valid zip tool
      // should not rely on that unless the file is corrupted
      val currentOffset = setFileOffset(header)
      val newOffset = currentOffset + sourceStart
      getFileOffset(header, newOffset)
    }

    // override files from target with files from source
    val sourceNames = sourceHeaders.map(getFileName).toSet
    val targetHeaders = getHeaders(targetModel).filterNot(h => sourceNames.contains(getFileName(h)))

    targetHeaders ++ sourceHeaders
  }

  private def truncateMetadata(metadata: Metadata, channel: FileChannel): FileChannel = {
    channel.truncate(getCentralDirStart(metadata))
  }

  protected def finalizeZip(
    metadata: Metadata,
    channel: FileChannel,
    metadataStart: Long
  ): Unit = {
    val outputStream = Channels.newOutputStream(channel.position(metadataStart))
    dumpMetadata(metadata, outputStream)
    channel.close()
  }


  protected def openFile(path: Path): FileChannel = {
    new RandomAccessFile(path.toFile, "rw").getChannel
  }

  protected def transferAll(
    from: FileChannel,
    to: FileChannel,
    startPos: Long,
    bytesToTransfer: Long
  ): Unit = {
    var remaining = bytesToTransfer
    var offset = startPos
    while (remaining > 0) {
      val transferred = to.transferFrom(from, /*position =*/ offset, /*count = */ remaining)
      offset += transferred
      remaining -= transferred
    }
  }

  protected def readMetadata(path: Path): Metadata

  protected def getCentralDirStart(metadata: Metadata): Long
  protected def setCentralDirStart(targetMetadata: Metadata, centralDirStart: Long): Unit

  protected def getHeaders(metadata: Metadata): Seq[Header]
  protected def setHeaders(metadata: Metadata, headers: Seq[Header]): Unit

  protected def getFileName(header: Header): String

  protected def setFileOffset(header: Header): Long
  protected def getFileOffset(header: Header, offset: Long): Unit

  protected def dumpMetadata(metadata: Metadata, outputStream: OutputStream): Unit

}
