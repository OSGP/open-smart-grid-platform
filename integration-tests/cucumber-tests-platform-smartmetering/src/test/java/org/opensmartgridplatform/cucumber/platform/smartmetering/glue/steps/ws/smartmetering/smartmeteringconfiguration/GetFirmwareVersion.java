//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getNullOrNonEmptyString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersionGas;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.DeviceFirmwareModuleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.FirmwareVersionRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetFirmwareVersionGasRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetFirmwareVersionRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.repositories.FirmwareModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GetFirmwareVersion {
  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired private FirmwareModuleRepository firmwareModuleRepository;

  @Autowired private DeviceFirmwareModuleSteps deviceFirmwareModuleSteps;

  @When("^the get firmware version request is received$")
  public void theGetFirmwareVersionRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final GetFirmwareVersionRequest getFirmwareVersionRequest =
        GetFirmwareVersionRequestFactory.fromParameterMap(requestData);

    final GetFirmwareVersionAsyncResponse getFirmwareVersionAsyncResponse =
        this.smartMeteringConfigurationClient.getFirmwareVersion(getFirmwareVersionRequest);

    assertThat(getFirmwareVersionAsyncResponse)
        .as("Get firmware version asyncResponse should not be null")
        .isNotNull();
    log.info("Get firmware version asyncResponse is received {}", getFirmwareVersionAsyncResponse);

    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            getFirmwareVersionAsyncResponse.getCorrelationUid());
  }

  @When("^the get firmware version gas request is received$")
  public void theGetFirmwareVersionGasRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final GetFirmwareVersionGasRequest gasRequest =
        GetFirmwareVersionGasRequestFactory.fromParameterMap(requestData);

    final GetFirmwareVersionGasAsyncResponse gasAsyncResponse =
        this.smartMeteringConfigurationClient.getFirmwareVersionGas(gasRequest);

    assertThat(gasAsyncResponse)
        .as("Get firmware version gas asyncResponse should not be null")
        .isNotNull();
    log.info("Get firmware version gas asyncResponse is received {}", gasAsyncResponse);

    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, gasAsyncResponse.getCorrelationUid());
  }

  @Then("^the firmware version result should be returned$")
  public void theFirmwareVersionResultShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest =
        FirmwareVersionRequestFactory.fromScenarioContext();

    final GetFirmwareVersionResponse getFirmwareVersionResponse =
        this.smartMeteringConfigurationClient.retrieveGetFirmwareVersionResponse(
            getFirmwareVersionAsyncRequest);

    assertThat(getFirmwareVersionResponse.getResult())
        .as("Get firmware version response has result null")
        .isNotNull();
    assertThat(getFirmwareVersionResponse.getResult())
        .as("Response should be OK")
        .isEqualTo(OsgpResultType.OK);

    final List<FirmwareVersion> firmwareVersions = getFirmwareVersionResponse.getFirmwareVersion();

    this.checkFirmwareVersionResult(settings, firmwareVersions);
  }

  @Then("^the firmware version gas result should be returned$")
  public void theFirmwareVersionGasResultShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final GetFirmwareVersionGasAsyncRequest gasAsyncRequest =
        new GetFirmwareVersionGasAsyncRequest();
    gasAsyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    gasAsyncRequest.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));

    final GetFirmwareVersionGasResponse gasResponse =
        this.smartMeteringConfigurationClient.retrieveGetFirmwareVersionGasResponse(
            gasAsyncRequest);

    assertThat(gasResponse.getResult())
        .as("Get firmware version response has result null")
        .isNotNull();
    assertThat(gasResponse.getResult()).as("Response should be OK").isEqualTo(OsgpResultType.OK);

    final FirmwareVersionGas firmwareVersion = gasResponse.getFirmwareVersion();

    this.checkFirmwareVersionGasResult(settings, firmwareVersion);
  }

  public void checkFirmwareVersionResult(
      final Map<String, String> settings, final List<FirmwareVersion> firmwareVersions) {

    final Map<FirmwareModule, String> expectedVersionsByModule =
        this.deviceFirmwareModuleSteps.getFirmwareModuleVersions(settings, true);

    assertThat(firmwareVersions.size())
        .as("Number of firmware modules")
        .isEqualTo(expectedVersionsByModule.size());

    for (final FirmwareVersion receivedFirmwareVersion : firmwareVersions) {
      assertThat(receivedFirmwareVersion.getFirmwareModuleType())
          .as("The received firmware module type is null")
          .isNotNull();

      assertThat(receivedFirmwareVersion.getVersion())
          .as("The received firmware version is null")
          .isNotNull();
      final String moduleDescription = receivedFirmwareVersion.getFirmwareModuleType().name();
      final String moduleVersion = receivedFirmwareVersion.getVersion();

      final FirmwareModule firmwareModule =
          this.firmwareModuleRepository.findByDescriptionIgnoreCase(moduleDescription);

      assertThat(firmwareModule)
          .as(
              "Received version \""
                  + moduleVersion
                  + "\" for unknown firmware module \""
                  + moduleDescription
                  + "\"")
          .isNotNull();

      assertThat(moduleVersion)
          .as(
              "Received version \""
                  + moduleVersion
                  + "\" for firmware module \""
                  + moduleDescription
                  + "\" which was not expected")
          .isNotNull();

      final String expectedVersion = expectedVersionsByModule.get(firmwareModule);
      assertThat(moduleVersion)
          .as("Version for firmware module \"" + moduleDescription + "\"")
          .isEqualTo(expectedVersion);
    }
  }

  public void checkFirmwareVersionGasResult(
      final Map<String, String> settings, final FirmwareVersionGas firmwareVersionGas) {

    assertThat(firmwareVersionGas.getFirmwareModuleType())
        .as("The received firmware module type is null")
        .isNotNull();

    assertThat(firmwareVersionGas.getVersion())
        .as("The received firmware version is null")
        .isNotNull();

    final String moduleDescription = firmwareVersionGas.getFirmwareModuleType().name();
    final String moduleVersion = Hex.encodeHexString(firmwareVersionGas.getVersion());

    final FirmwareModule firmwareModule =
        this.firmwareModuleRepository.findByDescriptionIgnoreCase(moduleDescription);

    assertThat(firmwareModule)
        .as(
            String.format(
                "Received version \"%s\" for unknown firmware module \"%s\"",
                moduleVersion, moduleDescription))
        .isNotNull();

    assertThat(moduleVersion)
        .as(
            String.format(
                "Received version \"%s\" for unknown firmware module \"%s\"",
                moduleVersion, moduleDescription))
        .isNotNull();

    final String expectedVersion =
        getNullOrNonEmptyString(settings, PlatformKeys.SIMPLE_VERSION_INFO, null);
    assertThat(moduleVersion)
        .as(String.format("Version for firmware module \"%s\"", moduleDescription))
        .isEqualTo(expectedVersion);
  }
}
