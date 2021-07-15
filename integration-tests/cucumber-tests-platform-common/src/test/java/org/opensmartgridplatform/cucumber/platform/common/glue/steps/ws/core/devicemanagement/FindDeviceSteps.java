/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceActivatedFilterType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceExternalManagedFilterType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceFilter;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceInMaintetanceFilterType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FirmwareModuleFilterType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class FindDeviceSteps {

  @Autowired private CoreDeviceManagementClient client;

  @When("receiving a find devices request")
  public void receivingAFindDevicesRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final FindDevicesRequest request = new FindDevicesRequest();

    if (requestParameters.containsKey(PlatformKeys.KEY_PAGE)) {
      request.setPage(getInteger(requestParameters, PlatformKeys.KEY_PAGE));
    }

    if (requestParameters.containsKey(PlatformKeys.KEY_PAGE_SIZE)) {
      request.setPageSize(getInteger(requestParameters, PlatformKeys.KEY_PAGE_SIZE));
    }

    if (requestParameters.containsKey(PlatformKeys.KEY_USE_PAGES)) {
      request.setUsePages(getBoolean(requestParameters, PlatformKeys.KEY_USE_PAGES));
    }

    if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)
        || requestParameters.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)
        || requestParameters.containsKey(PlatformKeys.ALIAS)
        || requestParameters.containsKey(PlatformKeys.KEY_CITY)
        || requestParameters.containsKey(PlatformKeys.KEY_POSTCODE)
        || requestParameters.containsKey(PlatformKeys.KEY_STREET)
        || requestParameters.containsKey(PlatformKeys.KEY_NUMBER)
        || requestParameters.containsKey(PlatformKeys.KEY_MUNICIPALITY)
        || requestParameters.containsKey(PlatformKeys.KEY_DEVICE_TYPE)
        || requestParameters.containsKey(PlatformKeys.KEY_MANUFACTURER)
        || requestParameters.containsKey(PlatformKeys.KEY_DEVICE_MODEL)
        || requestParameters.containsKey(PlatformKeys.KEY_DEVICE_EXTERNAL_MANAGED)
        || requestParameters.containsKey(PlatformKeys.KEY_DEVICE_ACTIVATED)
        || requestParameters.containsKey(PlatformKeys.KEY_DEVICE_MAINTENANCE)
        || requestParameters.containsKey(PlatformKeys.KEY_SORT_DIR)
        || requestParameters.containsKey(PlatformKeys.KEY_SORTED_BY)
        || requestParameters.containsKey(PlatformKeys.KEY_HAS_TECHNICAL_INSTALLATION)
        || requestParameters.containsKey(PlatformKeys.KEY_OWNER)
        || requestParameters.containsKey(PlatformKeys.KEY_FIRMWARE_MODULE_TYPE)
        || requestParameters.containsKey(PlatformKeys.KEY_FIRMWARE_MODULE_VERSION)
        || requestParameters.containsKey(PlatformKeys.KEY_EXACT_MATCH)) {
      final DeviceFilter deviceFilter = new DeviceFilter();

      if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
        deviceFilter.setDeviceIdentification(
            getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
        deviceFilter.setOrganisationIdentification(
            getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
      }

      if (requestParameters.containsKey(PlatformKeys.ALIAS)) {
        deviceFilter.setAlias(getString(requestParameters, PlatformKeys.ALIAS));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_CITY)) {
        deviceFilter.setCity(getString(requestParameters, PlatformKeys.KEY_CITY));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_POSTCODE)) {
        deviceFilter.setPostalCode(getString(requestParameters, PlatformKeys.KEY_POSTCODE));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_STREET)) {
        deviceFilter.setStreet(getString(requestParameters, PlatformKeys.KEY_STREET));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_NUMBER)) {
        deviceFilter.setNumber(getString(requestParameters, PlatformKeys.KEY_NUMBER));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_MUNICIPALITY)) {
        deviceFilter.setMunicipality(getString(requestParameters, PlatformKeys.KEY_MUNICIPALITY));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_TYPE)) {
        deviceFilter.setDeviceType(getString(requestParameters, PlatformKeys.KEY_DEVICE_TYPE));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_MANUFACTURER)) {
        deviceFilter.setManufacturer(getString(requestParameters, PlatformKeys.KEY_MANUFACTURER));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_MODEL)) {
        deviceFilter.setModel(getString(requestParameters, PlatformKeys.KEY_DEVICE_MODEL));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_EXTERNAL_MANAGED)) {
        deviceFilter.setDeviceExternalManaged(
            getEnum(
                requestParameters,
                PlatformKeys.KEY_DEVICE_EXTERNAL_MANAGED,
                DeviceExternalManagedFilterType.class));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_ACTIVATED)) {
        deviceFilter.setDeviceActivated(
            getEnum(
                requestParameters,
                PlatformKeys.KEY_DEVICE_ACTIVATED,
                DeviceActivatedFilterType.class));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_DEVICE_MAINTENANCE)) {
        deviceFilter.setDeviceInMaintenance(
            getEnum(
                requestParameters,
                PlatformKeys.KEY_DEVICE_MAINTENANCE,
                DeviceInMaintetanceFilterType.class));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_SORT_DIR)) {
        deviceFilter.setSortDir(getString(requestParameters, PlatformKeys.KEY_SORT_DIR));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_SORTED_BY)) {
        deviceFilter.setSortedBy(getString(requestParameters, PlatformKeys.KEY_SORTED_BY));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_HAS_TECHNICAL_INSTALLATION)) {
        deviceFilter.setHasTechnicalInstallation(
            getBoolean(requestParameters, PlatformKeys.KEY_HAS_TECHNICAL_INSTALLATION));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_OWNER)) {
        deviceFilter.setOwner(getString(requestParameters, PlatformKeys.KEY_OWNER));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_FIRMWARE_MODULE_TYPE)) {
        deviceFilter.setFirmwareModuleType(
            getEnum(
                requestParameters,
                PlatformKeys.KEY_FIRMWARE_MODULE_TYPE,
                FirmwareModuleFilterType.class));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_FIRMWARE_MODULE_VERSION)) {
        deviceFilter.setFirmwareModuleVersion(
            getString(requestParameters, PlatformKeys.KEY_FIRMWARE_MODULE_VERSION));
      }

      if (requestParameters.containsKey(PlatformKeys.KEY_EXACT_MATCH)) {
        deviceFilter.setExactMatch(getBoolean(requestParameters, PlatformKeys.KEY_EXACT_MATCH));
      }

      request.setDeviceFilter(deviceFilter);
    }

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.findDevices(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("the find devices response contains \"{int}\" device(s)")
  public void theFindDevicesResponseContainsDevices(final Integer numberOfDevices)
      throws Throwable {
    final FindDevicesResponse response =
        (FindDevicesResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response.getDevices().size()).isEqualTo((int) numberOfDevices);
  }

  @Then("the find devices response contains at index \"{int}\"")
  public void theFindDevicesResponseContainsAtIndex(
      final Integer index, final Map<String, String> expectedDevice) throws Throwable {
    final FindDevicesResponse response =
        (FindDevicesResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    DeviceSteps.checkDevice(expectedDevice, response.getDevices().get(index - 1));
  }
}
