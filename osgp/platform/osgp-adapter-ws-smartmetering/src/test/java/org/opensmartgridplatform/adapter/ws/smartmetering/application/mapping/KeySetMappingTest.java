/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData;

public class KeySetMappingTest {

  private static final byte[] BYTE_ARRAY = {1, 64, 127};
  private ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Method to check mapping of filled byte arrays. */
  private void checkMappingFilledArray(final byte[] byteArray) {

    assertThat(byteArray).isNotNull();
    assertThat(byteArray[0]).isEqualTo(BYTE_ARRAY[0]);
    assertThat(byteArray[1]).isEqualTo(BYTE_ARRAY[1]);
    assertThat(byteArray[2]).isEqualTo(BYTE_ARRAY[2]);
  }

  /** Tests the mapping of a KeySet object with empty byte arrays. */
  @Test
  public void testWithEmptyArrays() {

    // build test data
    final SetKeysRequestData keySetOriginal = new SetKeysRequestData();
    final byte[] authenticationKey = {};
    keySetOriginal.setAuthenticationKey(authenticationKey);
    final byte[] encryptionKey = {};
    keySetOriginal.setEncryptionKey(encryptionKey);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
        keySetMapped =
            this.configurationMapper.map(
                keySetOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
                    .class);

    // check mapping
    assertThat(keySetMapped).isNotNull();
    assertThat(keySetMapped.getAuthenticationKey()).isNotNull();
    assertThat(keySetMapped.getAuthenticationKey().length == 0).isTrue();
    assertThat(keySetMapped.getEncryptionKey()).isNotNull();
    assertThat(keySetMapped.getEncryptionKey().length == 0).isTrue();
  }

  /** Tests the mapping of a KeySet object with filled byte arrays. */
  @Test
  public void testWithFilledArrays() {

    // build test data
    final SetKeysRequestData keySetOriginal = new SetKeysRequestData();
    final byte[] authenticationKey = BYTE_ARRAY;
    keySetOriginal.setAuthenticationKey(authenticationKey);
    final byte[] encryptionKey = BYTE_ARRAY;
    keySetOriginal.setEncryptionKey(encryptionKey);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
        keySetMapped =
            this.configurationMapper.map(
                keySetOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
                    .class);

    // check mapping
    assertThat(keySetMapped).isNotNull();
    this.checkMappingFilledArray(keySetMapped.getAuthenticationKey());
    this.checkMappingFilledArray(keySetMapped.getEncryptionKey());
  }

  /** Tests the mapping of a KeySet object with byte arrays that are null. */
  // Test mapping with null arrays
  @Test
  public void testWithNullArrays() {

    // build test data
    final SetKeysRequestData keySetOriginal = new SetKeysRequestData();
    final byte[] authenticationKey = null;
    keySetOriginal.setAuthenticationKey(authenticationKey);
    final byte[] encryptionKey = null;
    keySetOriginal.setEncryptionKey(encryptionKey);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
        keySetMapped =
            this.configurationMapper.map(
                keySetOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
                    .class);

    // check mapping
    assertThat(keySetMapped).isNotNull();
    assertThat(keySetMapped.getAuthenticationKey()).isNull();
    assertThat(keySetMapped.getEncryptionKey()).isNull();
  }
}
