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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceLifecycleStatusSteps {

  @Autowired private CoreDeviceManagementClient deviceManagementClient;

  @Autowired private DeviceRepository DeviceRepository;

  @When("^a SetDeviceLifecycleStatus request is received$")
  public void aSetDeviceLifecycleStatusRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetDeviceLifecycleStatusRequest request = new SetDeviceLifecycleStatusRequest();
    request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    request.setDeviceLifecycleStatus(
        DeviceLifecycleStatus.valueOf(settings.get(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)));

    final SetDeviceLifecycleStatusAsyncResponse asyncResponse =
        this.deviceManagementClient.setDeviceLifecycleStatus(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the device lifecycle status in the database is$")
  public void theDeviceLifecycleStatusInTheDatabaseIs(final Map<String, String> settings)
      throws Throwable {
    final SetDeviceLifecycleStatusAsyncRequest asyncRequest =
        new SetDeviceLifecycleStatusAsyncRequest();
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    asyncRequest.setDeviceId(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    Wait.until(
        () -> {
          SetDeviceLifecycleStatusResponse response = null;
          try {
            response =
                this.deviceManagementClient.getSetDeviceLifecycleStatusResponse(asyncRequest);
          } catch (final Exception e) {
            // do nothing
          }
          assertThat(response).as("No response found for Set Device Lifecycle Status").isNotNull();
          assertThat(response.getResult()).isNotEqualTo(OsgpResultType.NOT_FOUND);
          assertThat(response.getResult())
              .as("Set Device Lifecycle Status result should be OK")
              .isEqualTo(OsgpResultType.OK);
        });

    final String deviceLifecycleStatus = settings.get(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS);
    final Device device =
        this.DeviceRepository.findByDeviceIdentification(
            settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    assertThat(device.getDeviceLifecycleStatus())
        .as("Device Lifecycle Status should be " + deviceLifecycleStatus)
        .isEqualTo(
            org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus.valueOf(
                deviceLifecycleStatus));
  }
}
