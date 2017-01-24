/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getInteger;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

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
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.StepsBase;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.support.ws.core.CoreDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindDeviceSteps extends StepsBase {

    @Autowired
    private CoreDeviceManagementClient client;

    @When("receiving a find devices request")
    public void receivingAFindDevicesRequest(final Map<String, String> requestParameters) throws Throwable
    {
    	FindDevicesRequest request = new FindDevicesRequest();
    	
    	if (requestParameters.containsKey(Keys.PAGE)) {
    		request.setPage(getInteger(requestParameters, Keys.PAGE));
    	}
    	
    	if (requestParameters.containsKey(Keys.PAGE_SIZE)) {
        	request.setPageSize(getInteger(requestParameters, Keys.PAGE_SIZE));
    	}
    	
    	if (requestParameters.containsKey(Keys.USE_PAGES)) {
        	request.setUsePages(getBoolean(requestParameters, Keys.USE_PAGES));
    	}
    	
    	if (requestParameters.containsKey(Keys.DEVICE_IDENTIFICATION) ||
    			requestParameters.containsKey(Keys.ORGANIZATION_IDENTIFICATION) ||
    			requestParameters.containsKey(Keys.ALIAS) ||
    			requestParameters.containsKey(Keys.CITY) ||
    			requestParameters.containsKey(Keys.POSTCODE) ||
    			requestParameters.containsKey(Keys.STREET) ||
    			requestParameters.containsKey(Keys.NUMBER) ||
    			requestParameters.containsKey(Keys.MUNICIPALITY) ||
    			requestParameters.containsKey(Keys.DEVICE_TYPE) ||
    			requestParameters.containsKey(Keys.MANUFACTURER) ||
    			requestParameters.containsKey(Keys.DEVICE_MODEL) ||
    			requestParameters.containsKey(Keys.DEVICE_EXTERNAL_MANAGED) ||
    			requestParameters.containsKey(Keys.DEVICE_ACTIVATED) ||
    			requestParameters.containsKey(Keys.DEVICE_MAINTENANCE) ||
    			requestParameters.containsKey(Keys.SORT_DIR) ||
    			requestParameters.containsKey(Keys.SORTED_BY) ||
    			requestParameters.containsKey(Keys.HAS_TECHNICAL_INSTALLATION) ||
    			requestParameters.containsKey(Keys.OWNER) ||
    			requestParameters.containsKey(Keys.FIRMWARE_MODULE_TYPE) ||
    			requestParameters.containsKey(Keys.FIRMWARE_MODULE_VERSION) ||
    			requestParameters.containsKey(Keys.EXACT_MATCH)) {
        	DeviceFilter deviceFilter = new DeviceFilter();
        	
        	if (requestParameters.containsKey(Keys.DEVICE_IDENTIFICATION)) {
        		deviceFilter.setDeviceIdentification(getString(requestParameters, Keys.DEVICE_IDENTIFICATION));
        	}
        	
        	if (requestParameters.containsKey(Keys.ORGANIZATION_IDENTIFICATION)) {
        		deviceFilter.setOrganisationIdentification(getString(requestParameters, Keys.DEVICE_IDENTIFICATION));
            }
        	
        	if (requestParameters.containsKey(Keys.ALIAS)) {
        		deviceFilter.setAlias(getString(requestParameters, Keys.ALIAS));
        	}
        	
			if (requestParameters.containsKey(Keys.CITY)) {
				deviceFilter.setCity(getString(requestParameters, Keys.CITY));
			}
			
			if (requestParameters.containsKey(Keys.POSTCODE)) {
				deviceFilter.setPostalCode(getString(requestParameters, Keys.POSTCODE));
			}
			
			if (requestParameters.containsKey(Keys.STREET)) {
				deviceFilter.setStreet(getString(requestParameters, Keys.STREET));
			}

			if (requestParameters.containsKey(Keys.NUMBER)) {
				deviceFilter.setNumber(getString(requestParameters, Keys.NUMBER));
			}

			if (requestParameters.containsKey(Keys.MUNICIPALITY)) {
				deviceFilter.setMunicipality(getString(requestParameters, Keys.MUNICIPALITY));
			}

			if (requestParameters.containsKey(Keys.DEVICE_TYPE)) {
				deviceFilter.setDeviceType(getString(requestParameters, Keys.DEVICE_TYPE));
			}

			if (requestParameters.containsKey(Keys.MANUFACTURER)) {
				deviceFilter.setManufacturer(getString(requestParameters, Keys.MANUFACTURER));
			}

			if (requestParameters.containsKey(Keys.DEVICE_MODEL)) {
				deviceFilter.setModel(getString(requestParameters, Keys.DEVICE_MODEL));
			}

			if (requestParameters.containsKey(Keys.DEVICE_EXTERNAL_MANAGED)) {
				deviceFilter.setDeviceExternalManaged(getEnum(requestParameters, Keys.DEVICE_EXTERNAL_MANAGED, DeviceExternalManagedFilterType.class));
			}

			if (requestParameters.containsKey(Keys.DEVICE_ACTIVATED)) {
				deviceFilter.setDeviceActivated(getEnum(requestParameters, Keys.DEVICE_ACTIVATED, DeviceActivatedFilterType.class));
			}
			
			if (requestParameters.containsKey(Keys.DEVICE_MAINTENANCE)) {
				deviceFilter.setDeviceInMaintenance(getEnum(requestParameters, Keys.DEVICE_MAINTENANCE, DeviceInMaintetanceFilterType.class));
			}
			
			if (requestParameters.containsKey(Keys.SORT_DIR)) {
				deviceFilter.setSortDir(getString(requestParameters, Keys.SORT_DIR));
			}
			
			if (requestParameters.containsKey(Keys.SORTED_BY)) {
				deviceFilter.setSortedBy(getString(requestParameters, Keys.SORTED_BY));
			}
			
			if (requestParameters.containsKey(Keys.HAS_TECHNICAL_INSTALLATION)) {
				deviceFilter.setHasTechnicalInstallation(getBoolean(requestParameters, Keys.HAS_TECHNICAL_INSTALLATION));
			}
			
			if (requestParameters.containsKey(Keys.OWNER)) {
				deviceFilter.setOwner(getString(requestParameters, Keys.OWNER));
			}
			
			if (requestParameters.containsKey(Keys.FIRMWARE_MODULE_TYPE)) {
				deviceFilter.setFirmwareModuleType(getEnum(requestParameters, Keys.FIRMWARE_MODULE_TYPE, FirmwareModuleFilterType.class));
			}
			
			if (requestParameters.containsKey(Keys.FIRMWARE_MODULE_VERSION)) {
				deviceFilter.setFirmwareModuleVersion(getString(requestParameters, Keys.FIRMWARE_MODULE_VERSION));
			}
			
			if (requestParameters.containsKey(Keys.EXACT_MATCH)) {
				deviceFilter.setExactMatch(getBoolean(requestParameters, Keys.EXACT_MATCH));
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
