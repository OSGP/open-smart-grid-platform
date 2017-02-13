/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceActivatedFilterType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceExternalManagedFilterType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceFilter;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceInMaintetanceFilterType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FirmwareModuleFilterType;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindDeviceSteps extends GlueBase {

    @Autowired
    private CoreDeviceManagementClient client;

    @When("receiving a find devices request")
    public void receivingAFindDevicesRequest(final Map<String, String> requestParameters) throws Throwable
    {
    	FindDevicesRequest request = new FindDevicesRequest();
    	
    	if (requestParameters.containsKey(Keys.KEY_PAGE)) {
    		request.setPage(getInteger(requestParameters, Keys.KEY_PAGE));
    	}
    	
    	if (requestParameters.containsKey(Keys.KEY_PAGE_SIZE)) {
        	request.setPageSize(getInteger(requestParameters, Keys.KEY_PAGE_SIZE));
    	}
    	
    	if (requestParameters.containsKey(Keys.KEY_USE_PAGES)) {
        	request.setUsePages(getBoolean(requestParameters, Keys.KEY_USE_PAGES));
    	}
    	
    	if (requestParameters.containsKey(Keys.KEY_DEVICE_IDENTIFICATION) ||
    			requestParameters.containsKey(Keys.KEY_ORGANIZATION_IDENTIFICATION) ||
    			requestParameters.containsKey(Keys.KEY_ALIAS) ||
    			requestParameters.containsKey(Keys.KEY_CITY) ||
    			requestParameters.containsKey(Keys.KEY_POSTCODE) ||
    			requestParameters.containsKey(Keys.KEY_STREET) ||
    			requestParameters.containsKey(Keys.KEY_NUMBER) ||
    			requestParameters.containsKey(Keys.KEY_MUNICIPALITY) ||
    			requestParameters.containsKey(Keys.KEY_DEVICE_TYPE) ||
    			requestParameters.containsKey(Keys.KEY_MANUFACTURER) ||
    			requestParameters.containsKey(Keys.KEY_DEVICE_MODEL) ||
    			requestParameters.containsKey(Keys.KEY_DEVICE_EXTERNAL_MANAGED) ||
    			requestParameters.containsKey(Keys.KEY_DEVICE_ACTIVATED) ||
    			requestParameters.containsKey(Keys.KEY_DEVICE_MAINTENANCE) ||
    			requestParameters.containsKey(Keys.KEY_SORT_DIR) ||
    			requestParameters.containsKey(Keys.KEY_SORTED_BY) ||
    			requestParameters.containsKey(Keys.KEY_HAS_TECHNICAL_INSTALLATION) ||
    			requestParameters.containsKey(Keys.KEY_OWNER) ||
    			requestParameters.containsKey(Keys.KEY_FIRMWARE_MODULE_TYPE) ||
    			requestParameters.containsKey(Keys.KEY_FIRMWARE_MODULE_VERSION) ||
    			requestParameters.containsKey(Keys.KEY_EXACT_MATCH)) {
        	DeviceFilter deviceFilter = new DeviceFilter();
        	
        	if (requestParameters.containsKey(Keys.KEY_DEVICE_IDENTIFICATION)) {
        		deviceFilter.setDeviceIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION));
        	}
        	
        	if (requestParameters.containsKey(Keys.KEY_ORGANIZATION_IDENTIFICATION)) {
        		deviceFilter.setOrganisationIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION));
            }
        	
        	if (requestParameters.containsKey(Keys.KEY_ALIAS)) {
        		deviceFilter.setAlias(getString(requestParameters, Keys.KEY_ALIAS));
        	}
        	
			if (requestParameters.containsKey(Keys.KEY_CITY)) {
				deviceFilter.setCity(getString(requestParameters, Keys.KEY_CITY));
			}
			
			if (requestParameters.containsKey(Keys.KEY_POSTCODE)) {
				deviceFilter.setPostalCode(getString(requestParameters, Keys.KEY_POSTCODE));
			}
			
			if (requestParameters.containsKey(Keys.KEY_STREET)) {
				deviceFilter.setStreet(getString(requestParameters, Keys.KEY_STREET));
			}

			if (requestParameters.containsKey(Keys.KEY_NUMBER)) {
				deviceFilter.setNumber(getString(requestParameters, Keys.KEY_NUMBER));
			}

			if (requestParameters.containsKey(Keys.KEY_MUNICIPALITY)) {
				deviceFilter.setMunicipality(getString(requestParameters, Keys.KEY_MUNICIPALITY));
			}

			if (requestParameters.containsKey(Keys.KEY_DEVICE_TYPE)) {
				deviceFilter.setDeviceType(getString(requestParameters, Keys.KEY_DEVICE_TYPE));
			}

			if (requestParameters.containsKey(Keys.KEY_MANUFACTURER)) {
				deviceFilter.setManufacturer(getString(requestParameters, Keys.KEY_MANUFACTURER));
			}

			if (requestParameters.containsKey(Keys.KEY_DEVICE_MODEL)) {
				deviceFilter.setModel(getString(requestParameters, Keys.KEY_DEVICE_MODEL));
			}

			if (requestParameters.containsKey(Keys.KEY_DEVICE_EXTERNAL_MANAGED)) {
				deviceFilter.setDeviceExternalManaged(getEnum(requestParameters, Keys.KEY_DEVICE_EXTERNAL_MANAGED, DeviceExternalManagedFilterType.class));
			}

			if (requestParameters.containsKey(Keys.KEY_DEVICE_ACTIVATED)) {
				deviceFilter.setDeviceActivated(getEnum(requestParameters, Keys.KEY_DEVICE_ACTIVATED, DeviceActivatedFilterType.class));
			}
			
			if (requestParameters.containsKey(Keys.KEY_DEVICE_MAINTENANCE)) {
				deviceFilter.setDeviceInMaintenance(getEnum(requestParameters, Keys.KEY_DEVICE_MAINTENANCE, DeviceInMaintetanceFilterType.class));
			}
			
			if (requestParameters.containsKey(Keys.KEY_SORT_DIR)) {
				deviceFilter.setSortDir(getString(requestParameters, Keys.KEY_SORT_DIR));
			}
			
			if (requestParameters.containsKey(Keys.KEY_SORTED_BY)) {
				deviceFilter.setSortedBy(getString(requestParameters, Keys.KEY_SORTED_BY));
			}
			
			if (requestParameters.containsKey(Keys.KEY_HAS_TECHNICAL_INSTALLATION)) {
				deviceFilter.setHasTechnicalInstallation(getBoolean(requestParameters, Keys.KEY_HAS_TECHNICAL_INSTALLATION));
			}
			
			if (requestParameters.containsKey(Keys.KEY_OWNER)) {
				deviceFilter.setOwner(getString(requestParameters, Keys.KEY_OWNER));
			}
			
			if (requestParameters.containsKey(Keys.KEY_FIRMWARE_MODULE_TYPE)) {
				deviceFilter.setFirmwareModuleType(getEnum(requestParameters, Keys.KEY_FIRMWARE_MODULE_TYPE, FirmwareModuleFilterType.class));
			}
			
			if (requestParameters.containsKey(Keys.KEY_FIRMWARE_MODULE_VERSION)) {
				deviceFilter.setFirmwareModuleVersion(getString(requestParameters, Keys.KEY_FIRMWARE_MODULE_VERSION));
			}
			
			if (requestParameters.containsKey(Keys.KEY_EXACT_MATCH)) {
				deviceFilter.setExactMatch(getBoolean(requestParameters, Keys.KEY_EXACT_MATCH));
			}
			
        	request.setDeviceFilter(deviceFilter);
    	}
    	
    	ScenarioContext.Current().put(Keys.RESPONSE, client.findDevices(request));
    }
    
    @Then("the find devices response contains \"([^\"]*)\" devices")
    public void theFindDevicesResponseContainsDevices(final Integer numberOfDevices) throws Throwable
    {
    	FindDevicesResponse response = (FindDevicesResponse) ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertTrue(response.getDevices().size() == numberOfDevices);
    }

    @Then("the find devices response contains at index \"([^\"]*)\"")
    public void theFindDevicesResponseContainsAtIndex(final Integer index, final Map<String, String> expectedDevice) throws Throwable
    {
    	FindDevicesResponse response = (FindDevicesResponse) ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	DeviceSteps.checkDevice(expectedDevice, response.getDevices().get(index - 1));
    }
}
