// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compress a file to a zip file. Use the default constructor {@link FileZipper#FileZipper()} to
 * create an instance with {@link Deflater.BEST_COMPRESSION} compression level and input file
 * deletion after the file has been zipped.
 *
 * <p>Alternatively, choose a compression level using constructor {@link FileZipper#FileZipper(int,
 * boolean) by passing a constant defined by the class {@link Deflater}}. Disable input file
 * deletion if desired.
 */
public class FileZipper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileZipper.class);

  private static final int ONE_MB = 1 * 1024 * 1024;
  private static final String ZIP = ".zip";

  private final int compressionLevel;

  private final boolean removeInputFile;

  public FileZipper() {
    this.compressionLevel = Deflater.BEST_COMPRESSION;
    this.removeInputFile = true;
  }

  public FileZipper(final int compressionLevel, final boolean removeInputFile) {
    this.compressionLevel = compressionLevel;
    this.removeInputFile = removeInputFile;
  }

  public int getCompressionLevel() {
    return this.compressionLevel;
  }

  public boolean isRemoveInputFile() {
    return this.removeInputFile;
  }

  public static String compressFileUsingDefaultSettings(final String filePath) throws IOException {
    final FileZipper fileZipper = new FileZipper();
    return fileZipper.compressFile(filePath);
  }

  public String compressFile(final String filePath) throws IOException {
    return this.compressFile(filePath, ONE_MB);
  }

  public String compressFile(final String filePath, final int inMemoryBufferSize)
      throws IOException {
    final String zipFilePath = this.getZipFilePath(filePath);
    final String zipEntryName = this.getZipEntryName(filePath);
    this.doCompressFile(filePath, zipFilePath, zipEntryName, inMemoryBufferSize);
    this.doRemoveInputFile(filePath);

    return zipFilePath;
  }

  public String compressFile(final String inputFilePath, final String outputFilePath)
      throws IOException {
    return this.compressFile(inputFilePath, outputFilePath, ONE_MB);
  }

  public String compressFile(
      final String inputFilePath, final String outputFilePath, final int inMemoryBufferSize)
      throws IOException {
    final String zipFilePath = this.getZipFilePath(outputFilePath);
    final String zipEntryName = this.getZipEntryName(inputFilePath);
    this.doCompressFile(inputFilePath, zipFilePath, zipEntryName, inMemoryBufferSize);
    this.doRemoveInputFile(inputFilePath);

    return zipFilePath;
  }

  private String getZipFilePath(final String filePath) {
    if (filePath.endsWith(ZIP)) {
      return filePath;
    }
    return filePath + ZIP;
  }

  private String getZipEntryName(final String inputFilePath) {
    return inputFilePath.substring(inputFilePath.lastIndexOf(File.separatorChar) + 1);
  }

  private void doCompressFile(
      final String inputFilePath,
      final String zipFilePath,
      final String zipEntryName,
      final int inMemoryBufferSize)
      throws IOException {
    try (FileInputStream fileInputStream = new FileInputStream(inputFilePath);
        final ZipOutputStream zipOutputStream = this.createZipOutputStream(zipFilePath)) {
      this.createZipEntry(zipEntryName, zipOutputStream);
      this.writeZipFile(fileInputStream, zipOutputStream, inMemoryBufferSize);
      zipOutputStream.closeEntry();
    }
    LOGGER.info("Compressed file [{}] written.", zipFilePath);
  }

  private void doRemoveInputFile(final String inputFilePath) {
    if (!this.removeInputFile) {
      return;
    }

    LOGGER.info("Deleting input file [{}]...", inputFilePath);
    final File csvFile = new File(inputFilePath);
    if (csvFile.delete()) {
      LOGGER.info("Input file deleted.");
    } else {
      LOGGER.warn("Input file not deleted!");
    }
  }

  private ZipOutputStream createZipOutputStream(final String zipFilePath)
      throws FileNotFoundException {
    final FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
    final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
    final ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
    zipOutputStream.setLevel(this.compressionLevel);

    return zipOutputStream;
  }

  private ZipEntry createZipEntry(final String zipEntryName, final ZipOutputStream zipOutputStream)
      throws IOException {
    final ZipEntry zipEntry = new ZipEntry(zipEntryName);
    zipOutputStream.putNextEntry(zipEntry);

    return zipEntry;
  }

  private void writeZipFile(
      final FileInputStream fileInputStream,
      final ZipOutputStream zipOutputStream,
      final int inMemoryBufferSize)
      throws IOException {
    final byte[] buffer = new byte[inMemoryBufferSize];
    int length;
    while ((length = fileInputStream.read(buffer)) > 0) {
      zipOutputStream.write(buffer, 0, length);
    }
  }
}
