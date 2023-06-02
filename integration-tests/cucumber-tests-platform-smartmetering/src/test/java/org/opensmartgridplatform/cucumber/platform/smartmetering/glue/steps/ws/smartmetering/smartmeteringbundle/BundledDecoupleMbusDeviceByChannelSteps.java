//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.DecoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class BundledDecoupleMbusDeviceByChannelSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a Decouple M-Bus Device By Channel action$")
  public void theBundleRequestContainsADeCoupleMbusDeviceByChannelAction(
      final Map<String, String> parameters) throws Throwable {

    final DecoupleMbusDeviceByChannelRequest action = new DecoupleMbusDeviceByChannelRequest();
    action.setChannel(Short.valueOf(parameters.get(PlatformSmartmeteringKeys.CHANNEL)));
    this.addActionToBundleRequest(action);
  }

  @Then(
      "^the Decouple M-Bus Device By Channel bundle response is \"([^\"]*)\" with Mbus Device \"([^\"]*)\"$")
  public void theDecoupleMbusDeviceByChannelBundleResponseIsWithMbusDevice(
      final String result, final String mbusDeviceIdentification) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(DecoupleMbusDeviceByChannelResponse.class);

    assertThat(((DecoupleMbusDeviceByChannelResponse) response).getMbusDeviceIdentification())
        .as("MbusDeviceIdentification")
        .isEqualTo(mbusDeviceIdentification);
    assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));
  }

  @Then(
      "^the Decouple M-Bus Device By Channel bundle response is \"([^\"]*)\" without Mbus Device$")
  public void theDecoupleMbusDeviceByChannelBundleResponseIsWithoutMbusDevice(final String result)
      throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(DecoupleMbusDeviceByChannelResponse.class);

    assertThat(((DecoupleMbusDeviceByChannelResponse) response).getMbusDeviceIdentification())
        .as("MbusDeviceIdentification")
        .isNull();
    assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));
  }
}
