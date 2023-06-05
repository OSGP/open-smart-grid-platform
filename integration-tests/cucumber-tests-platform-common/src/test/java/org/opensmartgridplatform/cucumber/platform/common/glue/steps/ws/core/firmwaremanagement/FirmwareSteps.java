// Copyright 2021 Smart Society Services B.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getHexDecoded;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getStringList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.DeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class FirmwareSteps {

  public static final String COMMUNICATION_MODULE_ACTIVE_FIRMWARE =
      "communication_module_active_firmware";
  public static final String FUNCTIONAL = "functional";
  public static final String MODULE_ACTIVE_FIRMWARE = "module_active_firmware";
  public static final String M_BUS = "m_bus";
  public static final String SECURITY = "security";
  public static final String ACTIVE_FIRMWARE = "active_firmware";
  public static final String M_BUS_DRIVER_ACTIVE_FIRMWARE = "m_bus_driver_active_firmware";
  public static final String SIMPLE_VERSION_INFO = "simple_version_info";

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  protected Firmware createAndGetFirmware(final Map<String, String> requestParameters) {
    final Firmware firmware = new Firmware();
    firmware.setIdentification(
        getString(requestParameters, PlatformKeys.FIRMWARE_FILE_IDENTIFICATION, null));
    firmware.setFilename(getString(requestParameters, PlatformKeys.FIRMWARE_FILE_FILENAME, null));
    firmware.setDescription(getString(requestParameters, PlatformKeys.FIRMWARE_DESCRIPTION, null));
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_FILE)) {
      firmware.setFile(getHexDecoded(requestParameters, PlatformKeys.FIRMWARE_FILE, null));
    }
    firmware.setPushToNewDevices(
        getBoolean(
            requestParameters,
            PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES,
            PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE));

    firmware.setFirmwareModuleData(new FirmwareModuleData());
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM)) {
      firmware
          .getFirmwareModuleData()
          .setModuleVersionComm(
              getString(requestParameters, PlatformKeys.FIRMWARE_MODULE_VERSION_COMM, null));
    }
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC)) {
      firmware
          .getFirmwareModuleData()
          .setModuleVersionFunc(
              getString(requestParameters, PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC, null));
    }
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_MA)) {
      firmware
          .getFirmwareModuleData()
          .setModuleVersionMa(
              getString(requestParameters, PlatformKeys.FIRMWARE_MODULE_VERSION_MA, null));
    }
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS)) {
      firmware
          .getFirmwareModuleData()
          .setModuleVersionMbus(
              getString(requestParameters, PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS, null));
    }
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_SEC)) {
      firmware
          .getFirmwareModuleData()
          .setModuleVersionSec(
              getString(requestParameters, PlatformKeys.FIRMWARE_MODULE_VERSION_SEC, null));
    }
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_MODULE_VERSION_M_BUS_DRIVER_ACTIVE)) {
      firmware
          .getFirmwareModuleData()
          .setModuleVersionMBusDriverActive(
              getString(
                  requestParameters,
                  PlatformKeys.FIRMWARE_MODULE_VERSION_M_BUS_DRIVER_ACTIVE,
                  null));
    }

    final List<String> deviceModelCodes =
        getStringList(requestParameters, PlatformKeys.DEVICEMODEL_MODELCODE, ";");
    final List<String> deviceModelDescs =
        getStringList(requestParameters, PlatformKeys.DEVICEMODEL_DESCRIPTION, ";");
    final List<String> deviceModelManus =
        getStringList(requestParameters, PlatformKeys.MANUFACTURER_NAME, ";");
    for (int i = 0; i < deviceModelCodes.size(); i++) {
      final DeviceModel deviceModel = new DeviceModel();
      deviceModel.setModelCode(deviceModelCodes.get(i));
      deviceModel.setDescription(deviceModelDescs.size() >= i + 1 ? deviceModelDescs.get(i) : null);
      deviceModel.setManufacturer(
          deviceModelManus.size() >= i + 1
              ? deviceModelManus.get(i)
              : PlatformDefaults.DEFAULT_MANUFACTURER_NAME);
      firmware.getDeviceModels().add(deviceModel);
    }

    return firmware;
  }

  protected void assertFirmwareFileExists(
      final String identification, final Map<String, String> firmwareFileProperties) {
    final Firmware expectedFirmware = this.createAndGetFirmware(firmwareFileProperties);
    final FirmwareFile databaseFirmwareFile =
        this.firmwareFileRepository.findByIdentification(identification);

    assertThat(databaseFirmwareFile)
        .isNotNull()
        .as("Firmware File {} should exist", identification);

    assertThat(databaseFirmwareFile.getDescription()).isEqualTo(expectedFirmware.getDescription());
    assertThat(databaseFirmwareFile.getFile()).isEqualTo(expectedFirmware.getFile());
    assertThat(databaseFirmwareFile.getFilename()).isEqualTo(expectedFirmware.getFilename());
  }

  protected void assertFirmwareFileHasModuleVersions(
      final String identification, final Map<String, String> expectedmoduleVersions) {
    final Firmware expectedFirmware = this.createAndGetFirmware(expectedmoduleVersions);

    final FirmwareFile databaseFirmwareFile =
        this.firmwareFileRepository.findByIdentification(identification);

    assertThat(databaseFirmwareFile)
        .isNotNull()
        .as("Firmware File {} should exist", identification);

    final Map<FirmwareModule, String> databaseModuleVersions =
        databaseFirmwareFile.getModuleVersions();

    assertThat(databaseModuleVersions).hasSameSizeAs(expectedmoduleVersions);

    final FirmwareModuleData expectedFirmwareModuleData = expectedFirmware.getFirmwareModuleData();

    for (final Entry<FirmwareModule, String> entry : databaseModuleVersions.entrySet()) {

      final String version = entry.getValue();

      switch (entry.getKey().getDescription()) {
        case COMMUNICATION_MODULE_ACTIVE_FIRMWARE:
          assertThat(expectedFirmwareModuleData.getModuleVersionComm()).isEqualTo(version);
          break;
        case FUNCTIONAL:
          assertThat(expectedFirmwareModuleData.getModuleVersionFunc()).isEqualTo(version);
          break;
        case M_BUS:
          assertThat(expectedFirmwareModuleData.getModuleVersionMbus()).isEqualTo(version);
          break;
        case M_BUS_DRIVER_ACTIVE_FIRMWARE:
          assertThat(expectedFirmwareModuleData.getModuleVersionMBusDriverActive())
              .isEqualTo(version);
          break;
        case MODULE_ACTIVE_FIRMWARE:
          assertThat(expectedFirmwareModuleData.getModuleVersionMa()).isEqualTo(version);
          break;
        case SECURITY:
          assertThat(expectedFirmwareModuleData.getModuleVersionSec()).isEqualTo(version);
          break;
        case SIMPLE_VERSION_INFO:
          assertThat(expectedFirmwareModuleData.getModuleVersionSimple()).isEqualTo(version);
          break;
        default:
      }
    }
  }

  protected void assertFirmwareFileHasDeviceModels(
      final String identification, final Map<String, String> expectedDeviceModels) {
    final Firmware expectedFirmware = this.createAndGetFirmware(expectedDeviceModels);

    final FirmwareFile databaseFirmwareFile =
        this.firmwareFileRepository.findByIdentification(identification);

    assertThat(databaseFirmwareFile)
        .isNotNull()
        .as("Firmware File {} should exist", identification);

    final SortedSet<org.opensmartgridplatform.domain.core.entities.DeviceModel>
        databaseDeviceModels = databaseFirmwareFile.getDeviceModels();

    final List<DeviceModel> deviceModels = expectedFirmware.getDeviceModels();

    assertThat(databaseDeviceModels).hasSameSizeAs(deviceModels);

    final List<String> databaseDeviceModelCodes =
        databaseDeviceModels.stream().map(ddm -> ddm.getModelCode()).collect(Collectors.toList());
    final List<String> deviceModelCodes =
        deviceModels.stream().map(dm -> dm.getModelCode()).collect(Collectors.toList());

    assertThat(deviceModelCodes).containsExactlyInAnyOrderElementsOf(databaseDeviceModelCodes);
  }
}
