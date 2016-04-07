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
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet;

public class KeySetMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Test mapping with filled arrays.
    @Test
    public void testWithFilledArrays() {
        // build test data
        final KeySet keySetOriginal = new KeySet();
        final byte[] authenticationKey = { 1, 64, 127 };
        keySetOriginal.setAuthenticationKey(authenticationKey);
        final byte[] encryptionKey = { 127, 63, 0 };
        keySetOriginal.setEncryptionKey(encryptionKey);
        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet keySetMapped = this.mapperFactory
                .getMapperFacade().map(keySetOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);
        // check mapping
        assertNotNull(keySetMapped);

        assertEquals(1, keySetMapped.getAuthenticationKey()[0]);
        assertEquals(64, keySetMapped.getAuthenticationKey()[1]);
        assertEquals(127, keySetMapped.getAuthenticationKey()[2]);

        assertEquals(127, keySetMapped.getEncryptionKey()[0]);
        assertEquals(63, keySetMapped.getEncryptionKey()[1]);
        assertEquals(0, keySetMapped.getEncryptionKey()[2]);

    }

    // Test mapping with empty arrays
    @Test
    public void testWithEmptyArrays() {
        // build test data
        final KeySet keySetOriginal = new KeySet();
        final byte[] authenticationKey = {};
        keySetOriginal.setAuthenticationKey(authenticationKey);
        final byte[] encryptionKey = {};
        keySetOriginal.setEncryptionKey(encryptionKey);
        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet keySetMapped = this.mapperFactory
                .getMapperFacade().map(keySetOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);
        // check mapping
        assertNotNull(keySetMapped);

        assertTrue(keySetMapped.getAuthenticationKey().length == 0);
        assertTrue(keySetMapped.getEncryptionKey().length == 0);
    }

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
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet keySetMapped = this.mapperFactory
                .getMapperFacade().map(keySetOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);
        // check mapping
        assertNotNull(keySetMapped);
        assertNull(keySetMapped.getAuthenticationKey());
        assertNull(keySetMapped.getEncryptionKey());
    }
}
