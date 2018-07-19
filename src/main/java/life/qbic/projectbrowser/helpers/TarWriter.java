/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects.
 * Copyright (C) "2016”  Christopher Mohr, David Wojnar, Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package life.qbic.projectbrowser.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;


public class TarWriter {
  private static final Logger LOG = LogManager.getLogger(TarWriter.class);
  // some default value
  private String rootFolderName = "qbic";
  // TODO Take Vaadin defaults!
  private final int DEFAULT_BUFFER_SIZE = 32768;
  private int MAX_BUFFER_SIZE = 65536;
  private int BUFFER_SIZE = DEFAULT_BUFFER_SIZE;
  
  // Standard in most modern tar applications
  private final int tar_record_size = 512;
  final int tar_block_size = tar_record_size * 20;

  private TarOutputStream tar = null;

  /**
   * Sets the root Folder Name. If the archive is extracted everything is extracted to this folder
   * 
   * @param rootFolderName
   */
  public void setRootFolderName(String rootFolderName) {
    this.rootFolderName = rootFolderName;
  }

  /**
   * Sets the buffer size to new value only if 0 < bufferSize <= MAX_BUFFER_SIZE.
   * 
   * @param bufferSize
   */
  public void setBufferSize(int bufferSize) {
    if (0 < bufferSize && bufferSize <= MAX_BUFFER_SIZE) {
      BUFFER_SIZE = bufferSize;
    }
  }


  /**
   * sets the buffer size to default
   */
  public void resetBufferSize() {
    BUFFER_SIZE = DEFAULT_BUFFER_SIZE;
  }

  /**
   * if another outputstream was already used it is first closed.
   * 
   * @param out
   */
  public void setOutputStream(OutputStream out) {
    if (tar != null) {
      try {
        tar.close();
      } catch (IOException e) {
        e.printStackTrace();
        LOG.error("closing previous stream failed.", e.getCause());
      }
    }
    this.tar = new TarOutputStream(out);
    tar.setLongFileMode(TarOutputStream.LONGFILE_POSIX);
    tar.setBigNumberMode(TarOutputStream.BIGNUMBER_POSIX);

  }

  /**
   * tries to close the tar stream
   */
  public void closeStream() {	  
    if (tar != null) {
      try {
    	tar.flush();
        //tar.close();
    	tar.finish();
      } catch (IOException e) {
        LOG.error("closing previous stream failed.", e.getCause());
      }
      tar = null;
    }
  }

  /**
   * writes entries into the Tar ball. Each key value pair of entries will be one entry.
   * entries::key is the TarEntry name. entry::value the inputStream that will be written to that
   * entry.
   * 
   * @param entry
   */
//  public void writeEntry(Map<String, AbstractMap.SimpleEntry<InputStream, Long>> entries) {
//    Set<Entry<String, SimpleEntry<InputStream, Long>>> entrySet = entries.entrySet();
//    Iterator<Entry<String, SimpleEntry<InputStream, Long>>> it = entrySet.iterator();
//    while (it.hasNext()) {
//      Entry<String, SimpleEntry<InputStream, Long>> entry = it.next();
//      this.writeEntry(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
//    }
//  }



  /**
   * Writes the entry into the tar ball. filSize has to match the size of the entry. It will be
   * written to the tar ball under the name entryName.
   * 
   * @param entryName
   * @param entry
   * @param fileSize
   */
  public void writeEntry(String entryName, InputStream entry, long fileSize) {
//    LOG.debug(entryName + " " + entry + " " + fileSize);
    TarEntry tar_entry = new TarEntry(entryName);
    tar_entry.setSize(fileSize);
    tar_entry.getRealSize();
    long totalWritten = 0;
    try {
      tar.putNextEntry(tar_entry);
      int bytesRead = 0;
      final byte[] buffer = new byte[BUFFER_SIZE];
      // System.out.println("File: " + entryName + ", Size: " + Long.toString(fileSize));


      while ((bytesRead = entry.read(buffer)) > 0) {
        //System.out.println("bytes read: " + Integer.toString(bytesRead) + " buffer.length: " +
       //Integer.toString(buffer.length));
        tar.write(buffer, 0, bytesRead);
        totalWritten += bytesRead;
        
        //if (totalWritten >= buffer.length) {
          // Avoid chunked encoding for small resources
          //tar.flush();
        //}
      }
      // System.out.println("bytesRead");
      tar.closeEntry();

      // try to close input stream
      if (entry != null) {
        entry.close();
      }
    } catch (IOException e1) {
      // e1.printStackTrace();
      if (e1 instanceof ClientAbortException) {
        LOG.info("client aborted download.");
        return;
      } else {
        e1.printStackTrace();
        LOG.error("writing entry to client failed", e1.getCause());
      }

    }
    //LOG.debug("Total Written: " + totalWritten);
  }

  /**
   * creates a String path out of the given parameters
   * 
   * @param paths
   * @return
   */
  public String getPath(String... paths) {
    StringBuilder sb = new StringBuilder(rootFolderName);
    for (String s : paths) {
      if (!s.startsWith(java.io.File.separator))
        sb.append(java.io.File.separator);
      sb.append(s);
    }
    return sb.toString();
  }

  public long computeTarLength(Map<String, SimpleEntry<InputStream, Long>> entries) {
    Set<Entry<String, SimpleEntry<InputStream, Long>>> entrySet = entries.entrySet();
    Iterator<Entry<String, SimpleEntry<InputStream, Long>>> it = entrySet.iterator();
    long[] tarLen = new long[entrySet.size()];
    int i = 0;
    while (it.hasNext()) {
      Entry<String, SimpleEntry<InputStream, Long>> entry = it.next();
      tarLen[i] = entry.getValue().getValue();
      i++;
    }
    return computeTarLength(tarLen, tar_record_size, tar_block_size);
  }

  public long computeTarLength2(Map<String, SimpleEntry<String, Long>> entries) {
    Set<Entry<String, SimpleEntry<String, Long>>> entrySet = entries.entrySet();
    Iterator<Entry<String, SimpleEntry<String, Long>>> it = entrySet.iterator();
    long[] tarLen = new long[entrySet.size()];
    String[] fileNames = new String[entrySet.size()];
    int i = 0;
    while (it.hasNext()) {
      Entry<String, SimpleEntry<String, Long>> entry = it.next();
      tarLen[i] = entry.getValue().getValue();
      fileNames[i] = entry.getKey();
      i++;
    }
    return computeTarLength(tarLen, tar_record_size, tar_block_size);
  }

  /**
   * Computes the size of an uncompressed tarball with the given parameters. See this for more
   * information: http://en.wikipedia.org/wiki/Tar_%28computing%29#Format_details IMPORTANT:
   * computed value might be slightly bigger than the real one. But it suffices for our purppose,
   * where we want to show download progress.
   * 
   * @param file_sizes in bytes
   * @param tar_record_size in bytes
   * @param tar_block_size in bytes
   * @return size of the whole tar ball
   */
  private long computeTarLength(long[] file_sizes, int tar_record_size, int tar_block_size) {
    // Every file has a header
    long length_tar_headers = (file_sizes.length + 2) * tar_record_size;
    long total_length_file_sizes = 0;
    long append_zeros = 0;
    for (int i = 0; i < file_sizes.length; i++) {
      // every file is saved uncomppressed, add just its file size
      total_length_file_sizes += file_sizes[i];
      // Each entry must be a multiple of the record size. it is not. entry will be filled with
      // zeros until it is.
      long mod = file_sizes[i] % tar_record_size;
      if (mod > 0) {

        append_zeros += tar_record_size - mod;
      }
    }

    // tar ball must be a multiple of block size. If it is not will be filled with zeros until it
    // is.
    long mod = (length_tar_headers + total_length_file_sizes + append_zeros) % tar_block_size;
    if (mod > 0) {
      mod = tar_block_size - mod;
    }
    return length_tar_headers + total_length_file_sizes + append_zeros + mod;
  }

  /**
   * Computes the size of an uncompressed tarball with the given parameters. See this for more
   * information: http://en.wikipedia.org/wiki/Tar_%28computing%29#Format_details Important: handles
   * different header sizes DOES NOT WORK DO NOT USE
   * 
   * @param file_sizes in bytes
   * @param tar_record_size in bytes
   * @param tar_block_size in bytes
   * @return size of the whole tar ball
   */
  @Deprecated
  private long computeTarLength(String[] fileName, long[] file_sizes, int tar_record_size,
      int tar_block_size) {
    // Every file has a header
    long total_length_file_sizes = 0;
    long append_zeros = 0;
    for (int i = 0; i < file_sizes.length; i++) {
      // every file is saved uncomppressed, add just its file size
      TarEntry entry = new TarEntry(fileName[i]);
      entry.setSize(file_sizes[i]);
      System.out.println(fileName[i]);
      System.out.println(file_sizes[i]);
      System.out.println(entry.getSize());
      System.out.println(entry.getSize());
      total_length_file_sizes += entry.getSize();
      // Each entry must be a multiple of the record size. it is not. entry will be filled with
      // zeros until it is.
      long mod = file_sizes[i] % tar_record_size;
      if (mod > 0) {

        append_zeros += tar_record_size - mod;
      }
    }

    // tar ball must be a multiple of block size. If it is not will be filled with zeros until it
    // is.
    long mod = (total_length_file_sizes + append_zeros) % tar_block_size;
    if (mod > 0) {
      mod = tar_block_size - mod;
    }
    return total_length_file_sizes + append_zeros + mod;
  }

  /**
   * returns the content type of this particular writer
   * 
   * @return
   */
  public String getContentType() {
    return "application/x-tar";
  }
}
