/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FileUtils {

  private FileUtils() {
    // Prevent instantiation of this utility class.
  }

  public static File findFileInFolder(final String folderPath, final String fileName) {
    final File folder = new File(folderPath);
    final File[] files = folder.listFiles();
    for (final File file : files) {
      if (file.getName().equals(fileName)) {
        return file;
      }
    }

    return null;
  }

  public static File findFileInFolderUsingFilePrefix(
      final String folderPath, final String filePrefix) {
    final File folder = new File(folderPath);
    final File[] files = folder.listFiles();
    for (final File file : files) {
      if (file.getName().startsWith(filePrefix)) {
        return file;
      }
    }

    return null;
  }

  public static long countNumberOfLinesInFile(final String inputFilePath) throws IOException {

    return countNumberOfLinesInFile(new File(inputFilePath));
  }

  public static long countNumberOfLinesInFile(final File inputFile) throws IOException {
    try (Stream<String> lines = Files.lines(inputFile.toPath(), StandardCharsets.UTF_8)) {
      return lines.count();
    }
  }
}
