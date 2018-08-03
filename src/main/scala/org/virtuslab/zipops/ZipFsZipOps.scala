package org.virtuslab.zipops

import java.net.URI
import java.io.File
import java.nio.file._
import java.util.function.Consumer

object ZipFsZipOps extends ZipFsZipOpsBase

trait ZipFsZipOpsBase extends ZipOps {

  def removeEntries(jarFile: File, classes: Iterable[String]): Unit = {
    withZipFs(jarFile) { fs =>
      classes.foreach { cls =>
        Files.deleteIfExists(fs.getPath(cls))
      }
    }
  }

  def mergeArchives(into: File, from: File): Unit = {
    withZipFs(into) { intoFs =>
      withZipFs(from) { fromFs =>
        Files
          .walk(fromFs.getPath("/"))
          .forEachOrdered(new Consumer[Path] {
            override def accept(t: Path): Unit = {
              if (Files.isDirectory(t)) {
                Files.createDirectories(intoFs.getPath(t.toString))
              } else {
                Files.copy(t,
                  intoFs.getPath(t.toString),
                  StandardCopyOption.COPY_ATTRIBUTES,
                  StandardCopyOption.REPLACE_EXISTING)
              }
            }
          })
      }
    }
    from.delete()
  }

  private def withZipFs[A](file: File, create: Boolean = false)(action: FileSystem => A): A = {
    withZipFs(fileToJarUri(file), create)(action)
  }

  private def fileToJarUri(jarFile: File): URI = {
    new URI("jar:" + jarFile.toURI.toString)
  }

  protected def withZipFs[A](uri: URI, create: Boolean)(action: FileSystem => A): A = {
    val env = new java.util.HashMap[String, String]
    if (create) env.put("create", "true")
    val fs = FileSystems.newFileSystem(uri, env)
    try action(fs)
    finally {
      fs.close()
    }
  }

}
