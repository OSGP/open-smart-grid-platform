/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

/**
 * Test for organisation entity.
 */
public class OrganisationTest {

    /**
     * Test domains functionality.
     */
    @Test
    public void domainsTest() {
        final Organisation organisation = new OrganisationBuilder().withOrganisationIdentification("Org").withName("Name")
                .withFunctionGroup(PlatformFunctionGroup.ADMIN).build();

        // Check empty
        assertTrue(organisation.getDomains() != null);
        assertEquals(0, organisation.getDomains().size());

        // Check store and read 1 element
        organisation.addDomain(PlatformDomain.COMMON);
        assertEquals(PlatformDomain.COMMON, organisation.getDomains().get(0));

        // Check store and read 2nd element
        organisation.addDomain(PlatformDomain.PUBLIC_LIGHTING);
        assertEquals(PlatformDomain.COMMON, organisation.getDomains().get(0));
        assertEquals(PlatformDomain.PUBLIC_LIGHTING, organisation.getDomains().get(1));

        // Check empty list
        organisation.setDomains(null);
        assertEquals(0, organisation.getDomains().size());
    }
}
