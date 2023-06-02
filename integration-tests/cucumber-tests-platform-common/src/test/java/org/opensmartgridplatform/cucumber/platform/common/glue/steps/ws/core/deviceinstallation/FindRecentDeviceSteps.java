//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class FindRecentDeviceSteps {

  @Autowired private CoreDeviceInstallationClient client;

  @When("receiving a find recent devices request")
  public void receivingAFindRecentDevicesRequest() throws Throwable {
    final FindRecentDevicesRequest request = new FindRecentDevicesRequest();

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.findRecentDevices(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("the find recent devices response contains \"{int}\" device(s)")
  public void theFindRecentDevicesResponseContains(final Integer numberOfDevices) {
    final FindRecentDevicesResponse response =
        (FindRecentDevicesResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    final List<Device> devices = response.getDevices();
    assertThat((devices != null) ? devices.size() : 0).isEqualTo((int) numberOfDevices);
  }

  @Then("the find recent devices response contains at index \"{int}\"")
  public void theFindRecentDevicesResponseContainsAtIndex(
      final Integer index, final Map<String, String> expectedDevice) throws Throwable {
    final FindRecentDevicesResponse response =
        (FindRecentDevicesResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    final Device device = response.getDevices().get(index - 1);
    assertThat(device).isNotNull();
    DeviceSteps.checkDevice(expectedDevice, device);
  }

  @Then("^the find recent devices response contains soap fault$")
  public void theAddDeviceResponseContainsSoapFault(final Map<String, String> expectedResult)
      throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
