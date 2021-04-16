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
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SetOwnerSteps {

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepo;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private AdminDeviceManagementClient client;

  @When("^receiving a set owner request(?: over OSGP)?$")
  public void receivingAFindDevicesRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final SetOwnerRequest request = new SetOwnerRequest();

    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    request.setOrganisationIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setOwner(request));
  }

  @Then("^the set owner async response contains$")
  public void theFindDevicesResponseContainsDevices(final Map<String, String> expectedDevice)
      throws Throwable {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof SetOwnerResponse)
        .isTrue();

    final SetOwnerResponse response =
        (SetOwnerResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);
    assertThat(response).isNotNull();
  }

  @Then("^the owner of device \"([^\"]*)\" has been changed$")
  public void theFindDevicesResponseContainsAtIndex(
      final String deviceIdentification, final Map<String, String> expectedOrganization)
      throws Throwable {

    final List<DeviceAuthorization> deviceAuthorization =
        Wait.untilAndReturn(
            () -> {
              final List<DeviceAuthorization> retval =
                  this.deviceAuthorizationRepo.findByDevice(
                      this.deviceRepository.findByDeviceIdentification(deviceIdentification));
              assertThat(retval).isNotNull();
              return retval;
            });

    assertThat(deviceAuthorization.get(0).getOrganisation().getOrganisationIdentification())
        .isEqualTo(
            getString(
                expectedOrganization,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
  }
}
