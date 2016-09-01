/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the organization steps
 */
public class OrganizationSteps {
    
	@Autowired
    private OrganisationRepository repo;

	/**
	 * Generic method to create an organization.
	 * 
	 * @param settings The settings to use to create the organization.
	 * @throws Throwable
	 */
    @Given("^an organization$")
    public void anOrganization(final Map<String, String> settings) throws Throwable {
    	Organisation entity = new Organisation(settings.get("OrganizationIdentification"), settings.get("Name"), settings.get("Prefix"), PlatformFunctionGroup.ADMIN );

    	// Add all the mandatory stuff.
    	entity.addDomain(PlatformDomain.COMMON);
    	
    	// TODO: Add all the optional stuff
    	
    	repo.save(entity);
    }

    /**
     * Generic method to check if the organization is created as expected in the database.
     * 
     * @param expectedEntity The expected settings.
     * @throws Throwable
     */
    @Then("^the entity organization exists$")
    public void thenTheEntityOrganizationExists(Map<String, String> expectedEntity) throws Throwable {
        Organisation entity = repo.findByOrganisationIdentification(expectedEntity.get("OrganizationIdentification"));
        
        Assert.assertEquals(expectedEntity.get("Name"), entity.getName());
        Assert.assertEquals(expectedEntity.get("Prefix"), entity.getPrefix());
        Assert.assertTrue(expectedEntity.get("FunctionGroup").toUpperCase().equals(entity.getFunctionGroup().toString()));
        Assert.assertTrue(expectedEntity.get("Enabled").toLowerCase().equals("true") == entity.isEnabled());
        List<String> expectedDomains = Arrays.asList(expectedEntity.get("Domains").split(";"));
        Assert.assertEquals(expectedDomains.size(), entity.getDomains().size());
        for (PlatformDomain domain : entity.getDomains()){
        	Assert.assertTrue(expectedDomains.contains(domain.toString()));
        }
    }
    
    /**
     * Verify that the organization is not created.
     * 
     * @param organizationIdentification
     * @throws Throwable
     */
    @Then("^the organization with organization identification \"([^\"]*)\" is removed$")
    public void the_organization_with_organization_identification_is_removed(String organizationIdentification) throws Throwable {
        Assert.assertNull(repo.findByOrganisationIdentification(organizationIdentification));
    }
    
    /**
     * Verify 
     * @param name
     * @throws Throwable
     */
    @Then("^the organization is with name \"([^\"]*)\" not created$")
    public void the_organization_is_with_name_not_created(String name) throws Throwable {
        Assert.assertNull(repo.findByName(name));
    }
}
