/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
 * Compress a text file to a zip file. Use the default constructor
 * {@link FileZipper#FileZipper()}to create an instance with
 * {@link Deflater.BEST_COMPRESSION} compression level. Alternatively, choose a
 * compression level using constructor {@link FileZipper#FileZipper(int) by
 * passing a constant defined by the class {@link Deflater}}.
 */
public class FileZipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileZipper.class);

    private static final int ONE_MB = 1 * 1024 * 1024;
    private static final String ZIP = ".zip";

    private final int compressionLevel;

    public FileZipper() {
        this.compressionLevel = Deflater.BEST_COMPRESSION;
    }

    public FileZipper(final int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public String compressFile(final String filePath) throws IOException {
        return this.compressFile(filePath, ONE_MB);
    }

    public String compressFile(final String filePath, final int inMemoryBufferSize) throws IOException {
        final String zipFilePath = this.getZipFilePath(filePath);
        final String zipEntryName = this.getZipEntryName(filePath);
        this.doCompressFile(filePath, zipFilePath, zipEntryName, inMemoryBufferSize);

        return zipFilePath;
    }

    public String compressFile(final String inputFilePath, final String outputFilePath) throws IOException {
        return this.compressFile(inputFilePath, outputFilePath, ONE_MB);
    }

    public String compressFile(final String inputFilePath, final String outputFilePath, final int inMemoryBufferSize)
            throws IOException {
        final String zipFilePath = this.getZipFilePath(outputFilePath);
        final String zipEntryName = this.getZipEntryName(inputFilePath);
        this.doCompressFile(inputFilePath, zipFilePath, zipEntryName, inMemoryBufferSize);

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

    private void doCompressFile(final String inputFilePath, final String zipFilePath, final String zipEntryName,
            final int inMemoryBufferSize) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(inputFilePath);
                final ZipOutputStream zipOutputStream = this.createZipOutputStream(zipFilePath)) {
            this.createZipEntry(zipEntryName, zipOutputStream);
            this.writeZipFile(fileInputStream, zipOutputStream, inMemoryBufferSize);
            zipOutputStream.closeEntry();
        }
        LOGGER.info("Compressed file [{}] written.", zipFilePath);
    }

    private ZipOutputStream createZipOutputStream(final String zipFilePath) throws FileNotFoundException {
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

    private void writeZipFile(final FileInputStream fileInputStream, final ZipOutputStream zipOutputStream,
            final int inMemoryBufferSize) throws IOException {
        final byte[] buffer = new byte[inMemoryBufferSize];
        int lenght;
        while ((lenght = fileInputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, lenght);
        }
    }
}
