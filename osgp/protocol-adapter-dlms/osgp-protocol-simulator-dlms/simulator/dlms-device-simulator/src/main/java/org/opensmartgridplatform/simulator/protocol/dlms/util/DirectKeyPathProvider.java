//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.util;

public class DirectKeyPathProvider extends KeyPathProvider {

  public DirectKeyPathProvider(
      final String authKeyDefaultPath,
      final String encKeyDefaultPath,
      final String masterKeyDefaultPath) {
    super(authKeyDefaultPath, encKeyDefaultPath, masterKeyDefaultPath);
  }

  @Override
  public String getAuthenticationKeyFile(final int logicalDeviceId) {
    return this.getAuthenticationKeyDefaultPath();
  }

  @Override
  public String getEncryptionKeyFile(final int logicalDeviceId) {
    return this.getEncryptionKeyDefaultPath();
  }

  @Override
  public String getMasterKeyFile(final int logicalDeviceId) {
    return this.getMasterKeyDefaultPath();
  }
}
