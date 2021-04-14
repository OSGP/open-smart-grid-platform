/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import org.apache.commons.codec.binary.Hex;

public class SetKeysRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 1573954141584647005L;

  private byte[] authenticationKey;

  private byte[] encryptionKey;

  private boolean generatedKeys;

  public SetKeysRequestDto(final byte[] authenticationKey, final byte[] encryptionKey) {
    this.authenticationKey = authenticationKey;
    this.encryptionKey = encryptionKey;
  }

  public byte[] getAuthenticationKey() {
    return this.authenticationKey;
  }

  public byte[] getEncryptionKey() {
    return this.encryptionKey;
  }

  public boolean isGeneratedKeys() {
    return this.generatedKeys;
  }

  public void setGeneratedKeys(final boolean generatedKeys) {
    this.generatedKeys = generatedKeys;
  }

  @Override
  public String toString() {
    return "KeySet [authenticationKey="
        + Hex.encodeHexString(this.authenticationKey)
        + ", encryptionKey="
        + Hex.encodeHexString(this.encryptionKey)
        + "]";
  }
}
