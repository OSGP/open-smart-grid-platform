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
