//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.adhocmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.FindAllDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.FindAllDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightMeasurementDevice;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class FindAllDevicesSteps {

  @Autowired private PublicLightingAdHocManagementClient publicLightingClient;

  @When("^receiving a find all device request$")
  public void receivingAFindAllDevicesRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final FindAllDevicesRequest request = new FindAllDevicesRequest();

    if (requestParameters.containsKey(PlatformKeys.KEY_PAGE_SIZE)) {
      request.setPageSize(getInteger(requestParameters, PlatformKeys.KEY_PAGE_SIZE));
    }
    if (requestParameters.containsKey(PlatformKeys.KEY_PAGE)) {
      request.setPage(getInteger(requestParameters, PlatformKeys.KEY_PAGE));
    }

    try {
      ScenarioContext.current()
          .put(
              PlatformPubliclightingKeys.RESPONSE,
              this.publicLightingClient.findAllDevices(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
    }
  }

  @Then("the find all device response contains {string} device(s)")
  public void theFindAllDevicesResponseContainsDevices(final String numberOfDevices)
      throws Throwable {
    final FindAllDevicesResponse response =
        (FindAllDevicesResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response.getDevicePage().getDevices().size())
        .isEqualTo(Integer.valueOf(numberOfDevices));
  }

  @Then("the find all device response contains at index {string}")
  public void theFindAllDevicesResponseContainsAtIndex(
      final String index, final Map<String, String> expectedDevice) throws Throwable {
    final FindAllDevicesResponse response =
        (FindAllDevicesResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    final Device actualDevice =
        response.getDevicePage().getDevices().get(Integer.valueOf(index) - 1);

    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_UID)) {
      assertThat(actualDevice.getDeviceUid())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_UID));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
      assertThat(actualDevice.getDeviceIdentification())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_POSTCODE)) {
      assertThat(actualDevice.getContainerPostalCode())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_POSTCODE));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_CITY)) {
      assertThat(actualDevice.getContainerCity())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_CITY));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_STREET)) {
      assertThat(actualDevice.getContainerStreet())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_STREET));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_NUMBER)) {
      assertThat(actualDevice.getContainerNumber())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_NUMBER));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_LATITUDE)) {
      assertThat(actualDevice.getGpsLatitude())
          .isEqualTo(getFloat(expectedDevice, PlatformKeys.KEY_LATITUDE));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_LONGITUDE)) {
      assertThat(actualDevice.getGpsLongitude())
          .isEqualTo(getFloat(expectedDevice, PlatformKeys.KEY_LONGITUDE));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_TYPE)) {
      assertThat(actualDevice.getDeviceType())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_TYPE));
    }
    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_ACTIVATED)) {
      assertThat(actualDevice.isActivated())
          .isEqualTo(getBoolean(expectedDevice, PlatformKeys.KEY_DEVICE_ACTIVATED));
    }
    if (expectedDevice instanceof Ssld) {
      final Ssld ssld = (Ssld) actualDevice;
      if (expectedDevice.containsKey(PlatformKeys.KEY_HAS_SCHEDULE)) {
        assertThat(ssld.isHasSchedule())
            .isEqualTo(getBoolean(expectedDevice, PlatformKeys.KEY_HAS_SCHEDULE));
      }
      if (expectedDevice.containsKey(PlatformKeys.KEY_PUBLICKEYPRESENT)) {
        assertThat(ssld.isPublicKeyPresent())
            .isEqualTo(getBoolean(expectedDevice, PlatformKeys.KEY_PUBLICKEYPRESENT));
      }
    }
    if (expectedDevice instanceof LightMeasurementDevice) {
      final LightMeasurementDevice lmd = (LightMeasurementDevice) actualDevice;
      if (expectedDevice.containsKey(PlatformKeys.KEY_LMD_DESCRIPTION)) {
        assertThat(lmd.getDescription())
            .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_LMD_DESCRIPTION));
      }
      if (expectedDevice.containsKey(PlatformKeys.KEY_LMD_CODE)) {
        assertThat(lmd.getCode()).isEqualTo(getString(expectedDevice, PlatformKeys.KEY_LMD_CODE));
      }
      if (expectedDevice.containsKey(PlatformKeys.KEY_LMD_COLOR)) {
        assertThat(lmd.getColor()).isEqualTo(getString(expectedDevice, PlatformKeys.KEY_LMD_COLOR));
      }
      if (expectedDevice.containsKey(PlatformKeys.KEY_LMD_DIGITAL_INPUT)) {
        assertThat(lmd.getDigitalInput())
            .isEqualTo(getShort(expectedDevice, PlatformKeys.KEY_LMD_DIGITAL_INPUT));
      }
    }
  }
}
