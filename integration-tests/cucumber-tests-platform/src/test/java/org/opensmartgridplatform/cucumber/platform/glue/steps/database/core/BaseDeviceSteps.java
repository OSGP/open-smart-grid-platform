// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.config.CoreDeviceConfiguration;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Ean;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EanRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDeviceSteps {

  @Autowired protected CoreDeviceConfiguration configuration;

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired protected DeviceModelRepository deviceModelRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private EanRepository eanRepository;

  @Autowired private OrganisationRepository organizationRepository;

  @Autowired protected ProtocolInfoRepository protocolInfoRepository;

  /**
   * Update an existing device with the given settings.
   *
   * @param device The Device to update
   * @param settings The settings to update the device with.
   * @return The Device
   */
  public Device updateDevice(Device device, final Map<String, String> settings) {

    // Now set the optional stuff
    if (settings.containsKey(PlatformKeys.KEY_TECHNICAL_INSTALLATION_DATE)
        && StringUtils.isNotBlank(settings.get(PlatformKeys.KEY_TECHNICAL_INSTALLATION_DATE))) {
      device.setTechnicalInstallationDate(
          getDate(settings, PlatformKeys.KEY_TECHNICAL_INSTALLATION_DATE).toInstant());
    }

    /*
     * Model code does not uniquely identify a device model, which is why
     * deviceModelRepository is changed to return a list of device models.
     * In the test data that is set up, there probably is only one device
     * model for the given model code, and just selecting the first device
     * model returned should work.
     *
     * A better solution might be to add the manufacturer in the scenario
     * data and do a lookup by manufacturer and model code, which should
     * uniquely define the device model.
     */
    final List<DeviceModel> deviceModels =
        this.deviceModelRepository.findByModelCode(
            getString(
                settings,
                PlatformKeys.KEY_DEVICE_MODEL,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
    final DeviceModel deviceModel = deviceModels.get(0);

    device.setDeviceModel(deviceModel);

    device.updateProtocol(
        this.protocolInfoRepository.findByProtocolAndProtocolVersion(
            getString(settings, PlatformKeys.KEY_PROTOCOL, PlatformDefaults.DEFAULT_PROTOCOL),
            getString(
                settings,
                PlatformKeys.KEY_PROTOCOL_VERSION,
                PlatformDefaults.DEFAULT_PROTOCOL_VERSION)));

    InetAddress inetAddress;
    try {
      inetAddress =
          InetAddress.getByName(
              getString(
                  settings, PlatformKeys.IP_ADDRESS, this.configuration.getDeviceNetworkAddress()));
    } catch (final UnknownHostException e) {
      inetAddress = InetAddress.getLoopbackAddress();
    }

    device.setBtsId(getInteger(settings, PlatformKeys.BTS_ID, null));
    device.setCellId(getInteger(settings, PlatformKeys.CELL_ID, null));

    device.updateRegistrationData(
        inetAddress.getHostAddress(),
        getString(settings, PlatformKeys.KEY_DEVICE_TYPE, PlatformDefaults.DEFAULT_DEVICE_TYPE));

    device.updateInMaintenance(
        getBoolean(settings, PlatformKeys.IN_MAINTENANCE, PlatformDefaults.IN_MAINTENANCE));
    device.setDeviceLifecycleStatus(
        getEnum(
            settings,
            PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS,
            DeviceLifecycleStatus.class,
            PlatformDefaults.DEFAULT_DEVICE_LIFECYCLE_STATUS));
    if (!Objects.equals(
        getString(
            settings,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION),
        "null")) {
      device.addOrganisation(
          getString(
              settings,
              PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
              PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }
    device.updateMetaData(
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

    device.setActivated(
        getBoolean(settings, PlatformKeys.KEY_ACTIVATED, PlatformDefaults.DEFAULT_ACTIVATED));
    final String integrationType =
        getString(
                settings,
                PlatformKeys.KEY_INTEGRATION_TYPE,
                PlatformDefaults.DEFAULT_INTEGRATION_TYPE)
            .toUpperCase();
    device.setIntegrationType(IntegrationType.valueOf(integrationType));

    device = this.deviceRepository.save(device);
    device = this.updateWithAuthorization(device, settings);
    this.addEanToDevice(device, settings);

    final String mastSegment =
        getString(
            settings,
            PlatformKeys.KEY_CDMA_MAST_SEGMENT,
            PlatformDefaults.DEFAULT_CDMA_MAST_SEGMENT);
    final Short batchNumber =
        getShort(
            settings,
            PlatformKeys.KEY_CDMA_BATCH_NUMBER,
            PlatformDefaults.DEFAULT_CDMA_BATCH_NUMBER);

    final CdmaSettings cdmaSettings =
        mastSegment == null && batchNumber == null
            ? null
            : new CdmaSettings(mastSegment, batchNumber);
    device.updateCdmaSettings(cdmaSettings);

    return device;
  }

  private Device updateWithAuthorization(final Device device, final Map<String, String> settings) {
    final String organizationIdentification =
        getString(
            settings,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    final Organisation organization = this.findOrganization(organizationIdentification);
    if (organization == null) {
      return device;
    }
    final DeviceFunctionGroup functionGroup =
        getEnum(
            settings,
            PlatformKeys.KEY_DEVICE_FUNCTION_GROUP,
            DeviceFunctionGroup.class,
            DeviceFunctionGroup.OWNER);
    final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
    this.deviceAuthorizationRepository.save(authorization);
    final Device savedDevice = this.deviceRepository.save(device);
    ScenarioContext.current()
        .put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, savedDevice.getDeviceIdentification());
    return savedDevice;
  }

  private Organisation findOrganization(final String organizationIdentification) {
    if (StringUtils.isBlank(organizationIdentification)) {
      return null;
    }
    return this.organizationRepository.findByOrganisationIdentification(organizationIdentification);
  }

  private void addEanToDevice(final Device device, final Map<String, String> settings) {

    final Long eanCode = getLong(settings, PlatformKeys.EAN_CODE);
    if (eanCode != null) {
      final String eanDescription =
          getString(
              settings, PlatformKeys.EAN_DESCRIPTION, PlatformDefaults.DEFAULT_EAN_DESCRIPTION);
      final Ean ean = new Ean(device, eanCode, eanDescription);
      this.eanRepository.save(ean);
    }
  }

  /**
   * Update a device entity given its device identification.
   *
   * @param deviceIdentification The deviceIdentification of the device to update.
   * @param settings The settings to update the device with.
   * @return The Device.
   */
  public Device updateDevice(
      final String deviceIdentification, final Map<String, String> settings) {
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    return this.updateDevice(device, settings);
  }

  public DeviceAuthorization setDefaultDeviceAuthorizationForDevice(final Device device) {
    return this.setDeviceAuthorizationForDeviceOwnedByOrganization(
        device, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
  }

  public DeviceAuthorization setDeviceAuthorizationForDeviceOwnedByOrganization(
      Device device, final String organizationIdentification) {

    device.addOrganisation(organizationIdentification);

    final Organisation organization =
        this.organizationRepository.findByOrganisationIdentification(organizationIdentification);

    device = this.deviceRepository.save(device);

    final DeviceAuthorization deviceAuthorization =
        device.addAuthorization(organization, DeviceFunctionGroup.OWNER);

    return this.deviceAuthorizationRepository.save(deviceAuthorization);
  }
}
