// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getHexDecoded;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.DeviceFirmwareModuleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.UpdateFirmwareRequestFactory;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.dto.valueobjects.HashTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Slf4j
@Transactional(value = "txMgrCore")
public class UpdateFirmware {

  @Value("${smartmetering.firmware.path}")
  private String firmwarePath;

  @Value("${smartmetering.firmware.imageidentifier.extention}")
  private String imageIdentifierExtension;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  @Autowired private SmartMeteringConfigurationClient client;

  @Autowired private DeviceFirmwareModuleSteps deviceFirmwareModuleSteps;

  @When("^the request for a firmware upgrade is received$")
  public void theRequestForAFirmwareUpgradeIsReceived(final Map<String, String> settings)
      throws Throwable {

    final UpdateFirmwareRequest request = UpdateFirmwareRequestFactory.fromParameterMap(settings);
    final UpdateFirmwareAsyncResponse asyncResponse = this.client.updateFirmware(request);

    assertThat(asyncResponse).as("asyncResponse should not be null").isNotNull();
    ScenarioContextHelper.saveAsyncResponse(asyncResponse);
  }

  @Then("^retrieving the update firmware response results in an exception$")
  public void retrievingTheUpdateFirmwareResponseResultsInAnException() throws Throwable {

    final UpdateFirmwareAsyncRequest asyncRequest =
        UpdateFirmwareRequestFactory.fromScenarioContext();

    try {
      this.client.getUpdateFirmwareResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @Then("^the update firmware result should be returned$")
  public void theUpdateFirmwareResultShouldBeReturned(final Map<String, String> settings)
      throws Throwable {

    final UpdateFirmwareAsyncRequest asyncRequest =
        UpdateFirmwareRequestFactory.fromParameterMapAsync(settings);
    final UpdateFirmwareResponse response = this.client.getUpdateFirmwareResponse(asyncRequest);

    assertThat(response.getResult()).as("result").isEqualTo(OsgpResultType.OK);
  }

  @Then("^the database should not be updated with the new device firmware$")
  public void theDatabaseShouldNotBeUpdatedWithTheNewDeviceFirmware(
      final Map<String, String> settings) throws Throwable {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    assertThat(device).as("Device " + deviceIdentification + " not found.").isNotNull();

    final FirmwareFile activeFirmwareFile = device.getActiveFirmwareFile();
    if (activeFirmwareFile == null) {
      /*
       * The device has no active firmware in the database, so the
       * firmware from the settings has not been linked to the device.
       */
      return;
    }

    final String moduleVersionComm = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM);
    final String moduleVersionMa = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MA);
    final String moduleVersionFunc = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC);

    assertThat(
            Objects.equals(moduleVersionComm, activeFirmwareFile.getModuleVersionComm())
                && Objects.equals(moduleVersionMa, activeFirmwareFile.getModuleVersionMa())
                && Objects.equals(moduleVersionFunc, activeFirmwareFile.getModuleVersionFunc()))
        .as(
            "Device "
                + deviceIdentification
                + " should not have firmware versions from the scenario after an unsuccessful update.")
        .isFalse();
  }

  @Given(
      "a firmware file and image identifier in a firmware file store and corresponding hash in database")
  public void aFirmwareFileAndImageIdentifierInAFirmwareFileStoreAndCorrespondingHashInDatabase(
      final Map<String, String> settings) throws Throwable {
    final String firmwareIdentification =
        getString(settings, PlatformKeys.FIRMWARE_FILE_IDENTIFICATION, null);

    final FirmwareFile firmwareFile =
        this.firmwareFileRepository.findByIdentificationOnly(firmwareIdentification);
    byte[] fileContent = null;
    if (settings.containsKey(PlatformKeys.FIRMWARE_FILE)) {
      fileContent = getHexDecoded(settings, PlatformKeys.FIRMWARE_FILE, null);
    }
    final String imageIdentifier =
        getString(settings, PlatformKeys.FIRMWARE_FILE_IMAGE_IDENTIFIER, null);
    final String hashType = getString(settings, PlatformKeys.FIRMWARE_HASH_TYPE, null);
    final HashTypeDto hashTypeDto = HashTypeDto.valueOf(hashType);
    this.storeFileContentAndHash(firmwareFile, fileContent, imageIdentifier, hashTypeDto);
  }

  private void storeFileContentAndHash(
      final FirmwareFile firmwareFile,
      final byte[] fileContent,
      final String imageIdentifier,
      final HashTypeDto hashTypeDto)
      throws IOException {
    final Path path = Paths.get(this.firmwarePath);
    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }
    if (fileContent != null) {
      final Path pathFirmware = Paths.get(this.firmwarePath, firmwareFile.getIdentification());
      if (!Files.exists(pathFirmware)) {
        Files.write(pathFirmware, fileContent);
      }
    }
    if (imageIdentifier != null) {
      final Path pathImageIdentifier =
          Paths.get(
              this.firmwarePath,
              firmwareFile.getIdentification() + "." + this.imageIdentifierExtension);
      if (!Files.exists(pathImageIdentifier)) {
        Files.write(pathImageIdentifier, imageIdentifier.getBytes());
      }
    }
    firmwareFile.setHash(this.calcHash(fileContent, hashTypeDto.getAlgorithmName()));
    firmwareFile.setHashType(hashTypeDto.name());
    this.firmwareFileRepository.save(firmwareFile);
  }

  private String calcHash(final byte[] filecontent, final String algorithm) {
    try {
      final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      final byte[] digest = messageDigest.digest(filecontent);
      return new BigInteger(1, digest).toString(16);
    } catch (final NoSuchAlgorithmException e) {
      log.error("Error calculating digest", e);
      return "";
    }
  }
}
