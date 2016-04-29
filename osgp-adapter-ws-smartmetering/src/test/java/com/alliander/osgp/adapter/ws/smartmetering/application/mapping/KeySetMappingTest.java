/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet;

public class KeySetMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final byte[] BYTE_ARRAY = { 1, 64, 127 };

    /** Tests the mapping of a KeySet object with filled byte arrays. */
    @Test
    public void testWithFilledArrays() {

        // build test data
        final KeySet keySetOriginal = new KeySet();
        final byte[] authenticationKey = BYTE_ARRAY;
        keySetOriginal.setAuthenticationKey(authenticationKey);
        final byte[] encryptionKey = BYTE_ARRAY;
        keySetOriginal.setEncryptionKey(encryptionKey);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet keySetMapped = this.configurationMapper
                .map(keySetOriginal, com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);

        // check mapping
        assertNotNull(keySetMapped);
        this.checkMappingFilledArray(keySetMapped.getAuthenticationKey());
        this.checkMappingFilledArray(keySetMapped.getEncryptionKey());

    }

    /** Tests the mapping of a KeySet object with empty byte arrays. */
    @Test
    public void testWithEmptyArrays() {

        // build test data
        final KeySet keySetOriginal = new KeySet();
        final byte[] authenticationKey = {};
        keySetOriginal.setAuthenticationKey(authenticationKey);
        final byte[] encryptionKey = {};
        keySetOriginal.setEncryptionKey(encryptionKey);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet keySetMapped = this.configurationMapper
                .map(keySetOriginal, com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);

        // check mapping
        assertNotNull(keySetMapped);
        assertNotNull(keySetMapped.getAuthenticationKey());
        assertTrue(keySetMapped.getAuthenticationKey().length == 0);
        assertNotNull(keySetMapped.getEncryptionKey());
        assertTrue(keySetMapped.getEncryptionKey().length == 0);
    }

    /** Tests the mapping of a KeySet object with byte arrays that are null. */
    // Test mapping with null arrays
    @Test
    public void testWithNullArrays() {

        // build test data
        final KeySet keySetOriginal = new KeySet();
        final byte[] authenticationKey = null;
        keySetOriginal.setAuthenticationKey(authenticationKey);
        final byte[] encryptionKey = null;
        keySetOriginal.setEncryptionKey(encryptionKey);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet keySetMapped = this.configurationMapper
                .map(keySetOriginal, com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);

        // check mapping
        assertNotNull(keySetMapped);
        assertNull(keySetMapped.getAuthenticationKey());
        assertNull(keySetMapped.getEncryptionKey());
    }

    /** Method to check mapping of filled byte arrays. */
    private void checkMappingFilledArray(final byte[] byteArray) {

        assertNotNull(byteArray);
        assertEquals(BYTE_ARRAY[0], byteArray[0]);
        assertEquals(BYTE_ARRAY[1], byteArray[1]);
        assertEquals(BYTE_ARRAY[2], byteArray[2]);

    }
}
