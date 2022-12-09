/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FirmwareFileCachingRepositoryTest {

  private static final String FIRMWARE_IDENTIFICATION_UNAVAILABLE = "unavailable";
  private static final String FIRMWARE_IDENTIFICATION = "fw";
  private static final byte[] FIRMWARE_FILE = new byte[] {0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55};

  private FirmwareFileCachingRepository firmwareFileCachingRepostitory;

  @BeforeEach
  public void setup() {
    final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    cache.put(FIRMWARE_IDENTIFICATION, FIRMWARE_FILE);

    this.firmwareFileCachingRepostitory = new FirmwareFileCachingRepository(cache);
  }

  @Test
  public void isAvailableShouldReturnTrueWhenFirmwareFileInCache() {
    // Arrange

    // Act
    final boolean actual = this.firmwareFileCachingRepostitory.isAvailable(FIRMWARE_IDENTIFICATION);

    // Assert
    assertThat(actual).isTrue();
  }

  @Test
  public void isAvailableShouldReturnFalseWhenFirmwareFileNotInCache() {
    // Arrange

    // Act
    final boolean actual =
        this.firmwareFileCachingRepostitory.isAvailable(FIRMWARE_IDENTIFICATION_UNAVAILABLE);

    // Assert
    assertThat(actual).isFalse();
  }

  @Test
  public void retrieveShouldReturnFirmwareFileWhenFirmwareFileInCache() {
    // Arrange
    final byte[] expected = FIRMWARE_FILE;

    // Act
    final byte[] actual = this.firmwareFileCachingRepostitory.retrieve(FIRMWARE_IDENTIFICATION);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void retrieveShouldReturnCopyOfFirmwareFile() {
    // Arrange
    final byte[] expected = FIRMWARE_FILE;
    final byte[] actual = this.firmwareFileCachingRepostitory.retrieve(FIRMWARE_IDENTIFICATION);
    actual[0]++; // Change the first byte in the retrieved array

    // Act
    // When the file is retrieved again, it should be the original file
    final byte[] actual2 = this.firmwareFileCachingRepostitory.retrieve(FIRMWARE_IDENTIFICATION);

    // Assert
    assertThat(actual2).isEqualTo(expected);
  }

  @Test
  public void retrieveShouldReturnNullWhenFirmwareFileNotInCache() {
    // Arrange
    // Nothing to do

    // Act
    final byte[] actual =
        this.firmwareFileCachingRepostitory.retrieve(FIRMWARE_IDENTIFICATION_UNAVAILABLE);

    // Assert
    assertThat(actual).isNull();
  }

  @Test
  public void storeShouldAddFirmwareFileToCache() {
    // Arrange
    final String firmwareIdentificationToAdd = "fw-to-add";
    final byte[] firmwareFileToAdd = firmwareIdentificationToAdd.getBytes();

    final int expectedSize = this.firmwareFileCachingRepostitory.getCache().size() + 1;
    final boolean expectedInCache = true;

    // Act
    this.firmwareFileCachingRepostitory.store(firmwareIdentificationToAdd, firmwareFileToAdd);
    final int actualSize = this.firmwareFileCachingRepostitory.getCache().size();
    final boolean actualInCache =
        this.firmwareFileCachingRepostitory.getCache().containsKey(firmwareIdentificationToAdd);

    // Assert
    assertThat(actualSize).isEqualTo(expectedSize);
    assertThat(actualInCache).isEqualTo(expectedInCache);
  }
}
