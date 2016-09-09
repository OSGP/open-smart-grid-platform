/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.WaitForEntity;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the organization steps
 */
public class OrganizationSteps {
    
	@Autowired
    private OrganisationRepository repo;
	
	/**
	 * Some defaults for the organization class.
	 */
	private String DEFAULT_ORGANIZATION = "An Organization";
	private String DEFAULT_NAME = "An Organization";
	private String DEFAULT_PREFIX = "Test";
	private PlatformFunctionGroup DEFAULT_PLATFORM_FUNCTION_GROUP = PlatformFunctionGroup.ADMIN;
	private PlatformDomain DEFAULT_PLATFORM_DOMAIN = PlatformDomain.COMMON;

	/**
	 * Generic method to create an organization.
	 * 
	 * @param settings The settings to use to create the organization.
	 * @throws Throwable
	 */
    @Given("^an organization$")
    public void anOrganization(Map<String, String> settings) throws Throwable {
    	Organisation entity = new Organisation(
			getString(settings, "OrganizationIdentification", DEFAULT_ORGANIZATION), 
			getString(settings, "Name", DEFAULT_NAME), 
			getString(settings, "Prefix", DEFAULT_PREFIX), 
			getEnum(settings, "PlatformFunctionGroup", PlatformFunctionGroup.class, DEFAULT_PLATFORM_FUNCTION_GROUP));

    	// Add all the mandatory stuff.
    	entity.addDomain(getEnum(settings, "PlatformDomain", PlatformDomain.class, DEFAULT_PLATFORM_DOMAIN));
    	
    	// TODO: Add all the optional stuff
    	repo.save(entity);

    	// Save the created id for the organization in the scenario context.
    	Organisation savedEntity = repo.findByName(getString(settings, "Name", DEFAULT_NAME));
    	ScenarioContext.Current().Data.put("OrganizationId", savedEntity.getId());
    }

    /**
     * Generic method to check if the organization is created as expected in the database.
     * 
     * @param expectedEntity The expected settings.
     * @throws Throwable
     */
    @Then("^the entity organization exists$")
    public void thenTheEntityOrganizationExists(Map<String, String> expectedEntity) throws Throwable {
    	
    	// TODO: Wait until the stuff is created.
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
     * Ensure that the organization is disabled.
     * 
     * @param organizationIdentification
     * @throws Throwable
     */
    @Then("^the organization with organization identification \"([^\"]*)\" should be disabled$")
    public void the_organization_with_organization_identification_should_be_disabled(String organizationIdentification) throws Throwable {
        Organisation entity = repo.findByOrganisationIdentification(organizationIdentification);

        Assert.assertTrue(entity.isEnabled() == false);
    }
    
    /**
     * Verify
     * @param name
     * @throws Throwable
     */
    @Then("^the organization with name \"([^\"]*)\" should not be created$")
    public void the_organization_with_name_should_not_be_created(String name) throws Throwable {
    	// TODO: cannot find symbol
    	//Assert.assertNull(repo.findByName(name));
    }
}
