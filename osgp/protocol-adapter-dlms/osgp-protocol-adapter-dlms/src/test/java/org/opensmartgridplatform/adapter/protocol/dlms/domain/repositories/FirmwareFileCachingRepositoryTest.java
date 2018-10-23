/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;

public class FirmwareFileCachingRepositoryTest {

    private static final String FIRMWARE_IDENTIFICATION_UNAVAILABLE = "unavailable";
    private static final String FIRMWARE_IDENTIFICATION = "fw";
    private static final byte[] FIRMWARE_FILE = new byte[] { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55 };;

    private FirmwareFileCachingRepository firmwareFileCachingRepostitory;

    @Before
    public void setup() {
        final Map<String, byte[]> cache = new ConcurrentHashMap<>();
        cache.put(FIRMWARE_IDENTIFICATION, FIRMWARE_FILE);

        this.firmwareFileCachingRepostitory = new FirmwareFileCachingRepository(cache);
    }

    @Test
    public void isAvailableShouldReturnTrueWhenFirmwareFileInCache() {
        // Arrange
        final boolean expected = true;

        // Act
        final boolean actual = this.firmwareFileCachingRepostitory.isAvailable(FIRMWARE_IDENTIFICATION);

        // Assert
        assertEquals(expected, actual);

    }

    @Test
    public void isAvailableShouldReturnFalseWhenFirmwareFileNotInCache() {
        // Arrange
        final boolean expected = false;

        // Act
        final boolean actual = this.firmwareFileCachingRepostitory.isAvailable(FIRMWARE_IDENTIFICATION_UNAVAILABLE);

        // Assert
        assertEquals(expected, actual);

    }

    @Test
    public void retrieveShouldReturnFirmwareFileWhenFirmwareFileInCache() {
        // Arrange
        final byte[] expected = FIRMWARE_FILE;

        // Act
        final byte[] actual = this.firmwareFileCachingRepostitory.retrieve(FIRMWARE_IDENTIFICATION);

        // Assert
        assertArrayEquals(expected, actual);
    }

    @Test
    public void retrieveShouldReturnNullWhenFirmwareFileNotInCache() {
        // Arrange
        // Nothing to do

        // Act
        final byte[] actual = this.firmwareFileCachingRepostitory.retrieve(FIRMWARE_IDENTIFICATION_UNAVAILABLE);

        // Assert
        assertNull(actual);
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
        final boolean actualInCache = this.firmwareFileCachingRepostitory.getCache()
                .containsKey(firmwareIdentificationToAdd);

        // Assert
        assertEquals(expectedSize, actualSize);
        assertEquals(expectedInCache, actualInCache);
    }

}
