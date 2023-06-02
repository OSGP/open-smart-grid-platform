//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.util;

import java.io.File;

/**
 * This class is used to obtain the correct path for keys for a logical device.
 *
 * <p>Each logical device corresponds with an authentication / encryption file that should be
 * supplied in the configurable paths. When both files are there, that correspond with a logicalId X
 * like this: {$authKeyDefaultPath}X and {$encKeyDefaultPath}X, than these files will be used for
 * this logical device
 */
public class KeyPathProvider {

  private final String authKeyDefaultPath;
  private final String encKeyDefaultPath;
  private final String masterKeyDefaultPath;

  public KeyPathProvider(
      final String authKeyDefaultPath,
      final String encKeyDefaultPath,
      final String masterKeyDefaultPath) {
    this.authKeyDefaultPath = authKeyDefaultPath;
    this.encKeyDefaultPath = encKeyDefaultPath;
    this.masterKeyDefaultPath = masterKeyDefaultPath;
  }

  public String getAuthenticationKeyFile(final int logicalDeviceId) {
    if (this.hasDeviceSpecificKeyFiles(logicalDeviceId)) {
      return this.authKeyDefaultPath + logicalDeviceId;
    } else {
      return this.authKeyDefaultPath;
    }
  }

  public String getEncryptionKeyFile(final int logicalDeviceId) {
    if (this.hasDeviceSpecificKeyFiles(logicalDeviceId)) {
      return this.encKeyDefaultPath + logicalDeviceId;
    } else {
      return this.encKeyDefaultPath;
    }
  }

  public String getMasterKeyFile(final int logicalDeviceId) {
    if (this.hasDeviceSpecificKeyFiles(logicalDeviceId)) {
      return this.masterKeyDefaultPath + logicalDeviceId;
    } else {
      return this.masterKeyDefaultPath;
    }
  }

  protected String getAuthenticationKeyDefaultPath() {
    return this.authKeyDefaultPath;
  }

  protected String getEncryptionKeyDefaultPath() {
    return this.encKeyDefaultPath;
  }

  protected String getMasterKeyDefaultPath() {
    return this.masterKeyDefaultPath;
  }

  private boolean hasDeviceSpecificKeyFiles(final int logicalDeviceId) {
    final boolean b1 = this.fileExists(this.authKeyDefaultPath + logicalDeviceId);
    final boolean b2 = this.fileExists(this.encKeyDefaultPath + logicalDeviceId);
    final boolean b3 = this.fileExists(this.masterKeyDefaultPath + logicalDeviceId);
    return b1 && b2 && b3;
  }

  private boolean fileExists(final String filename) {
    return new File(filename).exists();
  }
}
