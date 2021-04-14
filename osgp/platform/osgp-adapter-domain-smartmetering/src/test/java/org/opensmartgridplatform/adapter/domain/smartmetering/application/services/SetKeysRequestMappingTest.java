/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;

public class SetKeysRequestMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  // To test mapping when arrays are null
  @Test
  public void testWithNullArrays() {

    // build test data
    final byte[] authenthicationKey = null;
    final byte[] encryptionKey = null;
    final SetKeysRequestData keySet = new SetKeysRequestData(authenthicationKey, encryptionKey);

    // actual mapping
    final SetKeysRequestDto keySetDto =
        this.configurationMapper.map(keySet, SetKeysRequestDto.class);

    // check if mapping succeeded
    assertThat(keySetDto).isNotNull();
    assertThat(keySetDto.getAuthenticationKey()).isNull();
    assertThat(keySetDto.getEncryptionKey()).isNull();
  }

  // To test mapping when arrays are empty
  @Test
  public void testWithEmptyArrays() {
    // build test data
    final byte[] authenthicationKey = new byte[1];
    final byte[] encryptionKey = new byte[1];
    final SetKeysRequestData keySet = new SetKeysRequestData(authenthicationKey, encryptionKey);

    // actual mapping
    final SetKeysRequestDto keySetDto =
        this.configurationMapper.map(keySet, SetKeysRequestDto.class);

    // check if mapping succeeded
    assertThat(keySetDto).isNotNull();
    assertThat(keySetDto.getAuthenticationKey()).isNotNull();
    assertThat(keySetDto.getEncryptionKey()).isNotNull();
    assertThat(keySetDto.getAuthenticationKey().length)
        .isEqualTo(keySet.getAuthenticationKey().length);
    assertThat(keySet.getEncryptionKey().length).isEqualTo(keySetDto.getEncryptionKey().length);
  }

  // To test mapping when arrays hold a value
  @Test
  public void testWithArrays() {
    // build test data
    final byte[] authenthicationKey = {1};
    final byte[] encryptionKey = {1};
    final SetKeysRequestData keySet = new SetKeysRequestData(authenthicationKey, encryptionKey);

    // actual mapping
    final SetKeysRequestDto keySetDto =
        this.configurationMapper.map(keySet, SetKeysRequestDto.class);

    // check if mapping succeeded
    assertThat(keySetDto).isNotNull();
    assertThat(keySetDto.getAuthenticationKey()).isNotNull();
    assertThat(keySetDto.getEncryptionKey()).isNotNull();
    assertThat(keySetDto.getAuthenticationKey().length)
        .isEqualTo(keySet.getAuthenticationKey().length);
    assertThat(keySet.getEncryptionKey().length).isEqualTo(keySetDto.getEncryptionKey().length);
    assertThat(keySetDto.getAuthenticationKey()[0]).isEqualTo(keySet.getAuthenticationKey()[0]);
    assertThat(keySetDto.getEncryptionKey()[0]).isEqualTo(keySet.getEncryptionKey()[0]);
  }
}
