//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** See: https://www.baeldung.com/java-compress-and-uncompress */
public class ZipFileReader {

  private ZipFileReader() {
    // Prevent instantiation of this utility class.
  }

  public static void extractZipFile(final String inputFilePath, final String outputFolderPath)
      throws IOException {
    final File destDir = new File(outputFolderPath);
    final byte[] buffer = new byte[1024];
    final ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFilePath));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      final File newFile = newFile(destDir, zipEntry);
      final FileOutputStream fos = new FileOutputStream(newFile);
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
      fos.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
  }

  public static File newFile(final File destinationDir, final ZipEntry zipEntry)
      throws IOException {
    final File destFile = new File(destinationDir, zipEntry.getName());

    final String destDirPath = destinationDir.getCanonicalPath();
    final String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
