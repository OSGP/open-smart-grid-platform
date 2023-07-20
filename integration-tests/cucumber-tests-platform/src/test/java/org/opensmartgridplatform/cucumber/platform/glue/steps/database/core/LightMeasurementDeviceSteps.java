// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.LightMeasurementDeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class LightMeasurementDeviceSteps extends BaseDeviceSteps {

  @Autowired private LightMeasurementDeviceRepository lightMeasurementDeviceRepository;

  @Then("^the light measurement device exists")
  public void theLightMeasurementDeviceExists(final Map<String, String> settings) throws Throwable {
    final LightMeasurementDevice lmd =
        Wait.untilAndReturn(
            () -> {
              final LightMeasurementDevice entity =
                  this.lightMeasurementDeviceRepository.findByDeviceIdentification(
                      settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
              if (entity == null) {
                throw new Exception(
                    "Device with identification ["
                        + settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION)
                        + "]");
              }

              return entity;
            });

    if (settings.containsKey(PlatformKeys.ALIAS)) {
      assertThat(lmd.getAlias()).isEqualTo(getString(settings, PlatformKeys.ALIAS));
    }
    if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
      assertThat(lmd.getOwner().getOrganisationIdentification())
          .isEqualTo(getString(settings, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_POSTALCODE)) {
      assertThat(lmd.getContainerAddress().getPostalCode())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_POSTALCODE));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_CITY)) {
      assertThat(lmd.getContainerAddress().getCity())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_CITY));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_STREET)) {
      assertThat(lmd.getContainerAddress().getStreet())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_STREET));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_NUMBER)) {
      assertThat(lmd.getContainerAddress().getNumber())
          .isEqualTo(getInteger(settings, PlatformKeys.CONTAINER_NUMBER));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_MUNICIPALITY)) {
      assertThat(lmd.getContainerAddress().getMunicipality())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_MUNICIPALITY));
    }
    if (settings.containsKey(PlatformKeys.KEY_LATITUDE)) {
      assertThat(lmd.getGpsCoordinates().getLatitude())
          .isEqualTo(getFloat(settings, PlatformKeys.KEY_LATITUDE));
    }
    if (settings.containsKey(PlatformKeys.KEY_LONGITUDE)) {
      assertThat(lmd.getGpsCoordinates().getLongitude())
          .isEqualTo(getFloat(settings, PlatformKeys.KEY_LONGITUDE));
    }
    if (settings.containsKey(PlatformKeys.KEY_ACTIVATED)) {
      assertThat(lmd.isActivated()).isEqualTo(getBoolean(settings, PlatformKeys.KEY_ACTIVATED));
    }
    if (settings.containsKey(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)) {
      assertThat(lmd.getDeviceLifecycleStatus())
          .isEqualTo(
              getEnum(
                  settings, PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS, DeviceLifecycleStatus.class));
    }
    if (settings.containsKey(PlatformKeys.KEY_DEVICE_MODEL_MODELCODE)) {
      assertThat(lmd.getDeviceModel().getModelCode())
          .isEqualTo(getString(settings, PlatformKeys.KEY_DEVICE_MODEL_MODELCODE));
    }
    if (settings.containsKey(PlatformKeys.KEY_LMD_DESCRIPTION)) {
      assertThat(lmd.getDescription())
          .isEqualTo(getString(settings, PlatformKeys.KEY_LMD_DESCRIPTION));
    }
    if (settings.containsKey(PlatformKeys.KEY_LMD_CODE)) {
      assertThat(lmd.getCode()).isEqualTo(getString(settings, PlatformKeys.KEY_LMD_CODE));
    }
    if (settings.containsKey(PlatformKeys.KEY_LMD_COLOR)) {
      assertThat(lmd.getColor()).isEqualTo(getString(settings, PlatformKeys.KEY_LMD_COLOR));
    }
    if (settings.containsKey(PlatformKeys.KEY_LMD_DIGITAL_INPUT)) {
      assertThat(lmd.getDigitalInput())
          .isEqualTo(getShort(settings, PlatformKeys.KEY_LMD_DIGITAL_INPUT));
    }
  }

  @Given("^a light measurement device$")
  @Transactional("txMgrCore")
  public LightMeasurementDevice aLightMeasurementDevice(final Map<String, String> settings) {
    final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final LightMeasurementDevice lmd = new LightMeasurementDevice(deviceIdentification);

    final List<DeviceModel> deviceModels =
        this.deviceModelRepository.findByModelCode(
            getString(
                settings,
                PlatformKeys.KEY_DEVICE_MODEL,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
    final DeviceModel deviceModel = deviceModels.get(0);
    lmd.setDeviceModel(deviceModel);

    if (settings.containsKey(PlatformKeys.KEY_DEVICE_TYPE)) {
      InetAddress inetAddress;
      try {
        inetAddress =
            InetAddress.getByName(
                getString(
                    settings,
                    PlatformKeys.IP_ADDRESS,
                    this.configuration.getDeviceNetworkAddress()));
      } catch (final UnknownHostException e) {
        inetAddress = InetAddress.getLoopbackAddress();
      }
      lmd.updateRegistrationData(
          inetAddress.getHostAddress(), getString(settings, PlatformKeys.KEY_DEVICE_TYPE));
    }
    lmd.updateMetaData(
        getString(settings, PlatformKeys.ALIAS, PlatformDefaults.DEFAULT_ALIAS),
        new Address(
            getString(settings, PlatformKeys.KEY_CITY, PlatformDefaults.DEFAULT_CONTAINER_CITY),
            getString(
                settings, PlatformKeys.KEY_POSTCODE, PlatformDefaults.DEFAULT_CONTAINER_POSTALCODE),
            getString(settings, PlatformKeys.KEY_STREET, PlatformDefaults.DEFAULT_CONTAINER_STREET),
            getInteger(
                settings, PlatformKeys.KEY_NUMBER, PlatformDefaults.DEFAULT_CONTAINER_NUMBER),
            getString(
                settings,
                PlatformKeys.KEY_NUMBER_ADDITION,
                PlatformDefaults.DEFAULT_CONTAINER_NUMBER_ADDITION),
            getString(
                settings,
                PlatformKeys.KEY_MUNICIPALITY,
                PlatformDefaults.DEFAULT_CONTAINER_MUNICIPALITY)),
        new GpsCoordinates(
            settings.containsKey(PlatformKeys.KEY_LATITUDE)
                    && StringUtils.isNotBlank(settings.get(PlatformKeys.KEY_LATITUDE))
                ? getFloat(settings, PlatformKeys.KEY_LATITUDE, PlatformDefaults.DEFAULT_LATITUDE)
                : null,
            settings.containsKey(PlatformKeys.KEY_LONGITUDE)
                    && StringUtils.isNotBlank(settings.get(PlatformKeys.KEY_LONGITUDE))
                ? getFloat(settings, PlatformKeys.KEY_LONGITUDE, PlatformDefaults.DEFAULT_LONGITUDE)
                : null));
    lmd.setActivated(
        getBoolean(settings, PlatformKeys.KEY_ACTIVATED, PlatformDefaults.DEFAULT_ACTIVATED));

    lmd.setDescription(
        getString(
            settings, PlatformKeys.KEY_LMD_DESCRIPTION, PlatformDefaults.DEFAULT_LMD_DESCRIPTION));
    lmd.setCode(getString(settings, PlatformKeys.KEY_LMD_CODE, PlatformDefaults.DEFAULT_LMD_CODE));
    lmd.setColor(
        getString(settings, PlatformKeys.KEY_LMD_COLOR, PlatformDefaults.DEFAULT_LMD_COLOR));
    lmd.setDigitalInput(
        getShort(
            settings,
            PlatformKeys.KEY_LMD_DIGITAL_INPUT,
            PlatformDefaults.DEFAULT_LMD_DIGITAL_INPUT));

    this.setDefaultDeviceAuthorizationForDevice(lmd);

    return this.lightMeasurementDeviceRepository.findByDeviceIdentification(deviceIdentification);
  }

  @Given("^the light measurement devices$")
  @Transactional("txMgrCore")
  public void theLightMeasurementDevices() throws Throwable {
    this.createLightMeasurementDevices();
  }

  /**
   * Create the 4 light measurement devices and {@link DeviceAuthorization}s for the default
   * organization.
   */
  public void createLightMeasurementDevices() {
    final LightMeasurementDevice lmd01 =
        this.createLightMeasurementDevice("LMD-01", "N-01", "#c9eec9", (short) 1);

    // Set the last communication time to 2017-08-01 at 13:00 UTC
    final Instant lastCommunicationTimeLmd01 =
        ZonedDateTime.of(2017, 8, 1, 13, 0, 0, 0, ZoneOffset.UTC).toInstant();
    lmd01.setLastCommunicationTime(lastCommunicationTimeLmd01);
    this.lightMeasurementDeviceRepository.save(lmd01);

    this.createLightMeasurementDevice("LMD-02", "E-01", "#eec9c9", (short) 2);
    this.createLightMeasurementDevice("LMD-03", "S-01", "#c9c9ee", (short) 3);
    this.createLightMeasurementDevice("LMD-04", "W-01", "#eeeec9", (short) 4);
  }

  /** Create a single light measurement device, including rights for the default organization. */
  @Transactional("txMgrCore")
  public LightMeasurementDevice createLightMeasurementDevice(
      final String deviceIdentification,
      final String code,
      final String color,
      final short digitalInput) {

    return this.createLightMeasurementDevice(
        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION,
        deviceIdentification,
        code,
        color,
        digitalInput);
  }

  /**
   * Create a single light measurement device, including rights for the organization identified by
   * {@code organizationIdentification}.
   */
  @Transactional("txMgrCore")
  public LightMeasurementDevice createLightMeasurementDevice(
      final String organizationIdentification,
      final String deviceIdentification,
      final String code,
      final String color,
      final short digitalInput) {

    final String deviceType = "LMD";
    final InetAddress networkAddress = InetAddress.getLoopbackAddress();
    final Instant technicalInstallationDate = ZonedDateTime.now(ZoneId.of("UTC")).toInstant();
    final ProtocolInfo protocolInfo =
        this.protocolInfoRepository.findByProtocolAndProtocolVersion("IEC61850", "1.0");

    final LightMeasurementDevice lightMeasurementDevice =
        new LightMeasurementDevice(deviceIdentification);
    lightMeasurementDevice.setTechnicalInstallationDate(technicalInstallationDate);
    lightMeasurementDevice.updateRegistrationData(networkAddress.getHostAddress(), deviceType);
    lightMeasurementDevice.updateProtocol(protocolInfo);
    lightMeasurementDevice.updateInMaintenance(false);
    lightMeasurementDevice.setDescription(deviceIdentification);
    lightMeasurementDevice.setCode(code);
    lightMeasurementDevice.setColor(color);
    lightMeasurementDevice.setLastCommunicationTime(technicalInstallationDate);
    lightMeasurementDevice.setDigitalInput(digitalInput);

    // Both creates the device and adds the device authorization as owner for the identified
    // organization.
    this.setDeviceAuthorizationForDeviceOwnedByOrganization(
        lightMeasurementDevice, organizationIdentification);

    return this.lightMeasurementDeviceRepository.findByDeviceIdentification(deviceIdentification);
  }
}
