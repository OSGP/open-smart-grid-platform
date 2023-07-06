// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.CoupleMbusDeviceByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.CoupleMbusDeviceRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class CoupleDeviceSteps extends AbstractSmartMeteringSteps {

  @Autowired private SmartMeteringInstallationClient smartMeteringInstallationClient;

  @When("^the Couple G-meter \"([^\"]*)\" request is received for E-meter \"([^\"]*)\"$")
  public void theCoupleGMeterRequestIsReceivedForEMeter(final String gasMeter, final String eMeter)
      throws WebServiceSecurityException {

    final CoupleMbusDeviceRequest request =
        CoupleMbusDeviceRequestFactory.forGatewayMbusDevice(eMeter, gasMeter);
    final CoupleMbusDeviceAsyncResponse asyncResponse =
        this.smartMeteringInstallationClient.coupleMbusDevice(request);

    this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
  }

  @When("^the Couple G-meter \"([^\"]*)\" request is received for E-meter \"([^\"]*)\" with force$")
  public void theCoupleGMeterRequestIsReceivedForEMeterWithForce(
      final String gasMeter, final String eMeter) throws WebServiceSecurityException {

    final CoupleMbusDeviceRequest request =
        CoupleMbusDeviceRequestFactory.forGatewayMbusDeviceWithForce(eMeter, gasMeter);
    final CoupleMbusDeviceAsyncResponse asyncResponse =
        this.smartMeteringInstallationClient.coupleMbusDevice(request);

    this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
  }

  @When(
      "^the Couple G-meter \"([^\"]*)\" to E-meter \"([^\"]*)\" request is received for an unknown gateway$")
  public void theCoupleGMeterToEMeterRequestOnChannelIsReceivedForAnUnknownGateway(
      final String gasMeter, final String eMeter) throws WebServiceSecurityException {

    final CoupleMbusDeviceRequest request =
        CoupleMbusDeviceRequestFactory.forGatewayMbusDevice(eMeter, gasMeter);

    try {
      this.smartMeteringInstallationClient.coupleMbusDevice(request);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @When(
      "^the Couple G-meter \"([^\"]*)\" to E-meter \"([^\"]*)\" request is received for an inactive device$")
  public void theCoupleGMeterRequestOnChannelIsReceivedForAnInactiveDevice(
      final String gasMeter, final String eMeter) throws WebServiceSecurityException {

    final CoupleMbusDeviceRequest request =
        CoupleMbusDeviceRequestFactory.forGatewayMbusDevice(eMeter, gasMeter);

    try {
      this.smartMeteringInstallationClient.coupleMbusDevice(request);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @Then("^the Couple response has the following values$")
  public void theCoupleResponseIs(final Map<String, String> values)
      throws WebServiceSecurityException {
    final CoupleMbusDeviceAsyncRequest asyncRequest =
        CoupleMbusDeviceRequestFactory.fromScenarioContext();
    final CoupleMbusDeviceResponse response =
        this.smartMeteringInstallationClient.getCoupleMbusDeviceResponse(asyncRequest);

    if (values.containsKey(PlatformKeys.KEY_MBUS_DEVICE_IDENTIFICATION)) {
      final String mbusDeviceIdentification =
          values.get(PlatformKeys.KEY_MBUS_DEVICE_IDENTIFICATION);
      assertThat(response.getMbusDeviceIdentification()).isEqualTo(mbusDeviceIdentification);
    }
    if (values.containsKey(PlatformKeys.KEY_CHANNEL)) {
      final short channel = Short.parseShort(values.get(PlatformKeys.KEY_CHANNEL));
      assertThat(response.getChannelElementValues().getChannel()).isEqualTo(channel);
    }
    if (values.containsKey(PlatformKeys.KEY_PRIMARY_ADDRESS)) {
      final short primaryAddress = Short.parseShort(values.get(PlatformKeys.KEY_PRIMARY_ADDRESS));
      assertThat(response.getChannelElementValues().getPrimaryAddress()).isEqualTo(primaryAddress);
    }
  }

  @Then("^retrieving the Couple response results in an exception$")
  public void retrievingTheCoupleResponseResultsInAnException() throws WebServiceSecurityException {

    final CoupleMbusDeviceAsyncRequest asyncRequest =
        CoupleMbusDeviceRequestFactory.fromScenarioContext();

    try {
      this.smartMeteringInstallationClient.getCoupleMbusDeviceResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @When("^the Couple M-Bus Device By Channel request is received$")
  public void theCoupleMBusDeviceByChannelRequestIsReceived(final Map<String, String> settings)
      throws Throwable {
    final CoupleMbusDeviceByChannelRequest request =
        CoupleMbusDeviceByChannelRequestFactory.fromSettings(settings);

    final CoupleMbusDeviceByChannelAsyncResponse asyncResponse =
        this.smartMeteringInstallationClient.coupleMbusDeviceByChannel(request);

    this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
  }

  @Then("^the Couple M-Bus Device By Channel response is \"([^\"]*)\"$")
  public void theCoupleMBusDeviceByChannelResponseIs(final String status) throws Throwable {
    final CoupleMbusDeviceByChannelAsyncRequest asyncRequest =
        CoupleMbusDeviceByChannelRequestFactory.fromScenarioContext();

    final CoupleMbusDeviceByChannelResponse response =
        this.smartMeteringInstallationClient.getCoupleMbusDeviceByChannelResponse(asyncRequest);

    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(status);
  }
}
