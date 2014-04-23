/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.common

import java.io._
import java.nio.ByteBuffer
import java.nio.charset.Charset
import fm.common.Implicits._

object InputStreamResource {
  implicit def toInputStreamResource(resource: Resource[InputStream]): InputStreamResource =  resource match {
    case isr: InputStreamResource => isr
    case _ => InputStreamResource(resource, autoDecompress = false, autoBuffer = false)
  }
  
  def wrap(is: InputStream, fileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    InputStreamResource(SingleUseResource(is), fileName = fileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forFileOrResource(file: File, originalFileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{
      if (file.isFile && file.canRead) new FileInputStream(file) else Thread.currentThread.getContextClassLoader.getResourceAsStream(file.toString())
    }.map{ is: InputStream =>
      if (null == is) throw new IOException("Missing File or Classpath Resource: "+file)
      is
    }
    forFileImpl(resource, file, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forFile(file: File, originalFileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ new FileInputStream(file) }.map{ is: InputStream =>
      if (null == is) throw new IOException("Missing File: "+file)
      is
    }
    forFileImpl(resource, file, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forByteBuffer(buf: ByteBuffer, originalFileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ new ByteBufferInputStream(buf.duplicate()) }
    InputStreamResource(resource, fileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forResource(file: File, originalFileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ Thread.currentThread.getContextClassLoader.getResourceAsStream(file.toString()) }.map{ is: InputStream =>
      if (null == is) throw new IOException("Missing Classpath Resource: "+file)
      is
    }
    forFileImpl(resource, file, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  private def forFileImpl(resource: Resource[InputStream], file: File, originalFileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    val fileName: String = originalFileName.toBlankOption.getOrElse{ file.getName }
    InputStreamResource(resource, fileName = fileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
}

final case class InputStreamResource(resource: Resource[InputStream], fileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true) extends Resource[InputStream] with Logging {
  def isUsable: Boolean = resource.isUsable
  def isMultiUse: Boolean = resource.isMultiUse
  
  // If the input stream is null for some reason (e.g. file/resource doesn't exist) then this will
  // cause an exception to be thrown before map/flatMap the resource to make the exceptions easier to understand
  private def nullProtectedResource: Resource[InputStream] = resource.map { is: InputStream =>
    if (null == is) throw new IOException("InputStream is null!"+fileName.toBlankOption.map{ n: String => s"  fileName: $n"  })
    is
  }
  
  def use[T](f: InputStream => T): T = bufferedFilter(decompressFilter(nullProtectedResource)).use { is: InputStream =>
    try {
     f(is)
    } catch {
     case ex: Exception =>
       logger.error(s"InputStreamResource Exception, working on: $fileName, exception: ${ex.getMessage}")
       throw ex
   }
  }
  
  /**
   * Create a reader for this InputStream and use auto-detection for the charset encoding with a fallback of UTF-8 if the charset cannot be detected
   */
  def reader(): Resource[Reader] = readerWithDetectedCharset()
  
  /**
   * Create a reader for this InputStream using the given encoding or auto-detect the encoding if the parameter is blank
   */
  def reader(encoding: String): Resource[Reader] = {
    if (encoding.isNotBlank) flatMap{ is => SingleUseResource(new InputStreamReader(is, encoding)) } else readerWithDetectedCharset()
  }
  
  /**
   * Create a reader for this InputStream using the given encoding or auto-detect the encoding if the parameter is blank
   */
  def reader(charset: Charset): Resource[Reader] = flatMap{ is => SingleUseResource(new InputStreamReader(is, charset)) }
  
  def readToString(): String = readToString("")
  
  /** A helper to read the input stream to a string */
  def readToString(encoding: String): String = reader(encoding).use { reader: Reader =>
    val writer = new StringWriter()
    IOUtils.copy(reader, writer)
    writer.toString
  }
  
  /** A helper to read the input stream to a string */
  def readToString(charset: Charset): String = reader(charset).use { reader: Reader =>
    val writer = new StringWriter()
    IOUtils.copy(reader, writer)
    writer.toString
  }
  
  def readBytes(): Array[Byte] = use{ is: InputStream =>
    val os = new org.apache.commons.io.output.ByteArrayOutputStream
    IOUtils.copy(is, os)
    os.toByteArray()    
  }
  
  def md5: Array[Byte] = use{ IOUtils.md5 }
  def md5Hex: String = use{ IOUtils.md5Hex }
  def sha1: Array[Byte] = use{ IOUtils.sha1 }
  def sha1Hex: String = use{ IOUtils.sha1Hex }
  
  def writeTo(output: Resource[OutputStream]): Unit = output.use{ writeTo }
  
  def writeTo(output: OutputStream): Unit = use{ input: InputStream => IOUtils.copy(input, output) }
  
  def buffered(): Resource[BufferedInputStream] = flatMap{ _.toBufferedInputStream }

  def readerWithDetectedCharset(): Resource[Reader] = flatMap{ is: InputStream =>
    
    // Need a mark supported InputStream (e.g. BufferedInputStream) for doing the charset detection
    val markSupportedInputStream: InputStream = if (is.markSupported) is else new BufferedInputStream(is)
    
    val charsetName: String = IOUtils.detectCharsetName(markSupportedInputStream, useMarkReset = true).orElse{
      // If this is a multi-use resource let's go ahead and try charset detection on the full stream
      if (isMultiUse) use{ IOUtils.detectCharsetName(_, useMarkReset = false) } else None
    }.getOrElse("UTF-8")
    
    SingleUseResource(new InputStreamReader(markSupportedInputStream, charsetName))
  }
  
  /** Requires use() to be called so it will consume the Resource */
  def detectCharset(): Option[Charset] = detectCharsetName().map{ Charset.forName }
  
  /** Requires use() to be called so it will consume the Resource */
  def detectCharsetName(): Option[String] = use { is: InputStream => IOUtils.detectCharsetName(is, useMarkReset = false) }
  
  def bufferedReader(): Resource[BufferedReader] = reader() flatMap { r => Resource(new BufferedReader(r)) }
  def bufferedReader(encoding: String): Resource[BufferedReader] = reader(encoding) flatMap { r => Resource(new BufferedReader(r)) }
  
  def dataInput(): Resource[DataInput] = flatMap{ is => Resource(new DataInputStream(is)) }

  private def decompressFilter(resource: Resource[InputStream]): Resource[InputStream] = {
    val lowerFileName: String = fileName.toLowerCase
    
    if (!autoDecompress) resource
    else if (lowerFileName.endsWith(".tar.gz"))    untar(gunzip(resource))
    else if (lowerFileName.endsWith(".tgz"))       untar(gunzip(resource))
    else if (lowerFileName.endsWith(".tar.bz"))    untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tar.bz2"))   untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tar.bzip2")) untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tbz2"))      untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tbz"))       untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tar.xz"))    untar(unxz(resource))
    else if (lowerFileName.endsWith(".tar"))       untar(resource)
    else if (lowerFileName.endsWith(".gz"))        gunzip(resource)
    else if (lowerFileName.endsWith(".bzip2"))     bunzip2(resource)
    else if (lowerFileName.endsWith(".bz2"))       bunzip2(resource)
    else if (lowerFileName.endsWith(".bz"))        bunzip2(resource)
    else if (lowerFileName.endsWith(".snappy"))    unsnappy(resource)
    else if (lowerFileName.endsWith(".xz"))        unxz(resource)
    else if (lowerFileName.endsWith(".zip"))       unzip(resource)
    else if (lowerFileName.endsWith(".jar"))       unjar(resource)
    else resource
  }
  
  private def gunzip(r: Resource[InputStream]):   Resource[InputStream] = r.flatMap{ _.gunzip   }
  private def unsnappy(r: Resource[InputStream]): Resource[InputStream] = r.flatMap{ _.unsnappy }
  private def bunzip2(r: Resource[InputStream]):  Resource[InputStream] = r.flatMap{ _.bunzip2  }
  private def unxz(r: Resource[InputStream]):     Resource[InputStream] = r.flatMap{ _.unxz     }
  private def unzip(r: Resource[InputStream]):    Resource[InputStream] = r.flatMap{ _.unzip    }
  private def unjar(r: Resource[InputStream]):    Resource[InputStream] = r.flatMap{ _.unjar    }
  private def untar(r: Resource[InputStream]):    Resource[InputStream] = r.flatMap{ _.untar    }
  
  def showArchiveEntries(): Unit = resource.use { _.showArchiveEntries() }
  
  private def bufferedFilter(resource: Resource[InputStream]): Resource[InputStream] = {
    if(autoBuffer) resource.flatMap{ _.toBufferedInputStream } else resource
  }
}