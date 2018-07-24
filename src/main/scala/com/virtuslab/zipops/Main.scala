package com.virtuslab.zipops

import java.io.RandomAccessFile
import java.nio.channels.{ Channels, FileChannel }

import net.lingala.zip4j.core.{ HeaderReader, HeaderWriter }
import net.lingala.zip4j.model.{ ZipModel, FileHeader }
import java.nio.file._
import java.util.ArrayList

import scala.collection.JavaConverters._

object Main extends App {

  def printHeader(h: FileHeader): Unit = println {
    s"""Entry(
       | name = ${h.getFileName}${if (h.isDirectory) " (dir)" else ""}
       | compressedSize = ${h.getCompressedSize}
       | localHeaderOffset = ${h.getOffsetLocalHeader}
       |)""".stripMargin
  }

  def getPath(file: String, copy: Boolean = false): Path = {
    if (copy) {
      val copiedFile = file + ".cpy"
      Files.copy(Paths.get(file), Paths.get(copiedFile), StandardCopyOption.REPLACE_EXISTING)
    } else Paths.get(file)
  }

  def readModel(path: Path): ZipModel = {
    val file = new RandomAccessFile(path.toFile, "rw")
    val headerReader = new HeaderReader(file)
    val model = headerReader.readAllHeaders()
    file.close()
    model
  }

  def getHeaders(model: ZipModel): Seq[FileHeader] = {
    model.getCentralDirectory.getFileHeaders.asInstanceOf[ArrayList[FileHeader]].asScala
  }

  def printCentralDir(path: Path): Unit = {
    getHeaders(readModel(path)).foreach(printHeader)
  }

  def removeEntries(path: Path, toRemove: Set[String]): Unit = {
    val model = readModel(path)
    val headers = getHeaders(model)
    val clearedHeaders = headers.filterNot(header => toRemove.contains(header.getFileName))
    model.getCentralDirectory.setFileHeaders(asArrayList(clearedHeaders))

    val writeOffset = startOfCentralDir(model)
    val channel = createChannel(path)
    channel.truncate(writeOffset)
    val outputStream = Channels.newOutputStream(channel.position(writeOffset))
    val headerWriter = new HeaderWriter
    headerWriter.finalizeZipFile(model, outputStream)
  }

  private def asArrayList[A](clearedHeaders: Seq[A]): ArrayList[A] = {
    new ArrayList[A](clearedHeaders.asJava)
  }

  private def createChannel(path: Path) = {
    new RandomAccessFile(path.toFile, "rw").getChannel
  }

  def mergeArchives(to: Path, from: Path): Unit = {
    val toModel = readModel(to)
    val fromModel = readModel(from)

    val toChannel = createChannel(to)
    val fromChannel = createChannel(from)
    toChannel.truncate(startOfCentralDir(toModel))
    // todo use loop (while not all transferred...)
    val fromStart = toChannel.size()
    val fromLength = startOfCentralDir(fromModel)
    transferAll(toChannel, fromChannel, fromStart, fromLength)
    fromChannel.close()

    val fromHeaders = getHeaders(fromModel)
    fromHeaders.foreach { header =>
      // potentially offsets should be updated for each header
      // not only in central directory but a valid extractor
      // should not rely on that unless jar is corrupted
      val currOffset = header.getOffsetLocalHeader
      val newOffset = currOffset + fromStart
      header.setOffsetLocalHeader(newOffset)
    }
    val centralDirStart = fromStart + fromLength
    val toHeaders = getHeaders(toModel)
    val newHeaders = toHeaders ++ fromHeaders
    toModel.getCentralDirectory.setFileHeaders(asArrayList(newHeaders))
    toModel.getEndCentralDirRecord.setOffsetOfStartOfCentralDir(centralDirStart)

    val outputStream = Channels.newOutputStream(toChannel.position(centralDirStart))
    val headerWriter = new HeaderWriter
    headerWriter.finalizeZipFile(toModel, outputStream)
    toChannel.close()
  }

  private def transferAll(to: FileChannel, from: FileChannel, startPos: Long, bytesToTransfer: Long): Unit = {
    var remaining = bytesToTransfer
    var offset = startPos
    while (remaining > 0) {
      val transferred = to.transferFrom(from, /*position =*/ offset, /*count = */ remaining)
      offset += transferred
      remaining -= transferred
    }
  }

  private def startOfCentralDir(model: ZipModel) = {
    model.getEndCentralDirRecord.getOffsetOfStartOfCentralDir
  }

//  val path = getPath("/home/lukasz/dev/test/test.jar", copy = true)
//  printCentralDir(path)
//  removeEntries(path, Set("test/A1", "test/B2"))
//  println("AFTER")
//  printCentralDir(path)

  val to = getPath("./test/1.zip", copy = true)
  val from = getPath("./test/2.zip", copy = false)
  println("TO")
  printCentralDir(to)
  println("FROM")
  printCentralDir(from)
  mergeArchives(to, from)
  println("AFTER")
  printCentralDir(to)
}
