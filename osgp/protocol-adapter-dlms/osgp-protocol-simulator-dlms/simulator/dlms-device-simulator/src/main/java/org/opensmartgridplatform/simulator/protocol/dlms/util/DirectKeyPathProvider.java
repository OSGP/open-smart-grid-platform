/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
