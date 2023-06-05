// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.inputparsers.DateInputParser;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public abstract class BaseDeviceBuilder<T extends BaseDeviceBuilder<T>> {
  Long version = PlatformSmartmeteringDefaults.VERSION;
  String deviceIdentification = PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION;
  String deviceType = PlatformSmartmeteringDefaults.DEVICE_TYPE;
  boolean isActivated = PlatformSmartmeteringDefaults.IS_ACTIVATED;
  String containerCity = PlatformSmartmeteringDefaults.CONTAINER_CITY;
  String containerStreet = PlatformSmartmeteringDefaults.CONTAINER_STREET;
  Float gpsLatitude = PlatformSmartmeteringDefaults.GPS_LATITUDE;
  Float gpsLongitude = PlatformSmartmeteringDefaults.GPS_LONGITUDE;
  String containerPostalCode = PlatformSmartmeteringDefaults.CONTAINER_POSTAL_CODE;
  Integer containerNumber = PlatformSmartmeteringDefaults.CONTAINER_NUMBER;
  String containerNumberAddition = PlatformSmartmeteringDefaults.DEFAULT_CONTAINER_NUMBER_ADDITION;
  ProtocolInfo protocolInfo = null;
  Integer baseTransceiverStationId = null;
  Integer cellId = null;
  InetAddress networkAddress = PlatformSmartmeteringDefaults.NETWORK_ADDRESS;
  String containerMunicipality = PlatformSmartmeteringDefaults.CONTAINER_MUNICIPALITY;
  String alias = PlatformSmartmeteringDefaults.ALIAS;
  boolean inMaintenance = PlatformSmartmeteringDefaults.IN_MAINTENANCE;
  String gatewayDeviceIdentification = PlatformSmartmeteringDefaults.GATEWAY_DEVICE_IDENTIFICATION;
  Date technicalInstallationDate = PlatformSmartmeteringDefaults.TECHNICAL_INSTALLATION_DATE;
  DeviceModel deviceModel = PlatformSmartmeteringDefaults.DEVICE_MODEL;
  DeviceLifecycleStatus deviceLifeCycleStatus = PlatformDefaults.DEFAULT_DEVICE_LIFECYCLE_STATUS;

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDeviceBuilder.class);

  public T setVersion(final Long version) {
    this.version = version;
    return (T) this;
  }

  public T setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return (T) this;
  }

  public T setDeviceType(final String deviceType) {
    this.deviceType = deviceType;
    return (T) this;
  }

  public T setIsActivated(final boolean isActivated) {
    this.isActivated = isActivated;
    return (T) this;
  }

  public T setContainerCity(final String containerCity) {
    this.containerCity = containerCity;
    return (T) this;
  }

  public T setContainerStreet(final String containerStreet) {
    this.containerStreet = containerStreet;
    return (T) this;
  }

  public T setGpsLatitude(final Float gpsLatitude) {
    this.gpsLatitude = gpsLatitude;
    return (T) this;
  }

  public T setGpsLongitude(final Float gpsLongitude) {
    this.gpsLongitude = gpsLongitude;
    return (T) this;
  }

  public T setContainerPostalCode(final String containerPostalCode) {
    this.containerPostalCode = containerPostalCode;
    return (T) this;
  }

  public T setContainerNumber(final Integer containerNumber) {
    this.containerNumber = containerNumber;
    return (T) this;
  }

  public T setContainerNumberAddition(final String containerNumberAddition) {
    this.containerNumberAddition = containerNumberAddition;
    return (T) this;
  }

  public T setProtocolInfo(final ProtocolInfo protocolInfo) {
    this.protocolInfo = protocolInfo;
    return (T) this;
  }

  public T setBaseTransceiverStationId(final Integer baseTransceiverStationId) {
    this.baseTransceiverStationId = baseTransceiverStationId;
    return (T) this;
  }

  public T setCellId(final Integer cellId) {
    this.cellId = cellId;
    return (T) this;
  }

  public T setNetworkAddress(final InetAddress networkAddress) {
    this.networkAddress = networkAddress;
    return (T) this;
  }

  public T setContainerMunicipality(final String containerMunicipality) {
    this.containerMunicipality = containerMunicipality;
    return (T) this;
  }

  public T setAlias(final String alias) {
    this.alias = alias;
    return (T) this;
  }

  public T setInMaintenance(final Boolean inMaintenance) {
    this.inMaintenance = inMaintenance;
    return (T) this;
  }

  public T setGatewayDevice(final String gatewayDeviceIdentification) {
    this.gatewayDeviceIdentification = gatewayDeviceIdentification;
    return (T) this;
  }

  public T setTechnicalInstallationDate(final Date technicalInstallationDate) {
    this.technicalInstallationDate = technicalInstallationDate;
    return (T) this;
  }

  public T setDeviceModel(final DeviceModel deviceModel) {
    this.deviceModel = deviceModel;
    return (T) this;
  }

  public T setDeviceLifecycleStatus(final DeviceLifecycleStatus deviceLifecycleStatus) {
    this.deviceLifeCycleStatus = deviceLifecycleStatus;
    return (T) this;
  }

  public T withSettings(final Map<String, String> inputSettings) {

    if (inputSettings.containsKey(PlatformSmartmeteringKeys.VERSION)) {
      this.setVersion(Long.valueOf(inputSettings.get(PlatformSmartmeteringKeys.VERSION)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION)) {
      this.setDeviceIdentification(
          inputSettings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.DEVICE_TYPE)) {
      this.setDeviceType(inputSettings.get(PlatformSmartmeteringKeys.DEVICE_TYPE));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.IS_ACTIVATED)) {
      this.setIsActivated(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.IS_ACTIVATED)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CONTAINER_CITY)) {
      this.setContainerCity(inputSettings.get(PlatformSmartmeteringKeys.CONTAINER_CITY));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CONTAINER_STREET)) {
      this.setContainerStreet(inputSettings.get(PlatformSmartmeteringKeys.CONTAINER_STREET));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.GPS_LATITUDE)) {
      this.setGpsLatitude(Float.valueOf(inputSettings.get(PlatformSmartmeteringKeys.GPS_LATITUDE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.GPS_LONGITUDE)) {
      this.setGpsLongitude(
          Float.valueOf(inputSettings.get(PlatformSmartmeteringKeys.GPS_LONGITUDE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CONTAINER_POSTAL_CODE)) {
      this.setContainerPostalCode(
          inputSettings.get(PlatformSmartmeteringKeys.CONTAINER_POSTAL_CODE));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CONTAINER_NUMBER)) {
      this.setContainerNumber(
          Integer.valueOf(inputSettings.get(PlatformSmartmeteringKeys.CONTAINER_NUMBER)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CONTAINER_MUNICIPALITY)) {
      this.setContainerMunicipality(
          inputSettings.get(PlatformSmartmeteringKeys.CONTAINER_MUNICIPALITY));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.ALIAS)) {
      this.setAlias(inputSettings.get(PlatformSmartmeteringKeys.ALIAS));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.IN_MAINTENANCE)) {
      this.setInMaintenance(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.IN_MAINTENANCE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.TECHNICAL_INSTALLATION_DATE)) {
      this.setTechnicalInstallationDate(
          DateInputParser.parse(
              inputSettings.get(PlatformSmartmeteringKeys.TECHNICAL_INSTALLATION_DATE)));
    }
    if (inputSettings.containsKey(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)) {
      this.setDeviceLifecycleStatus(
          DeviceLifecycleStatus.valueOf(
              inputSettings.get(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.GATEWAY_DEVICE_IDENTIFICATION)) {
      this.setGatewayDevice(
          inputSettings.get(PlatformSmartmeteringKeys.GATEWAY_DEVICE_IDENTIFICATION));
    }

    this.setBaseTransceiverStationId(
        ReadSettingsHelper.getInteger(
            inputSettings, PlatformSmartmeteringKeys.KEY_BASE_TRANSCEIVER_STATION_ID, null));
    this.setCellId(
        ReadSettingsHelper.getInteger(inputSettings, PlatformSmartmeteringKeys.KEY_CELL_ID, null));

    if (inputSettings.containsKey(PlatformSmartmeteringKeys.NETWORK_ADDRESS)) {
      if (StringUtils.isBlank(inputSettings.get(PlatformSmartmeteringKeys.NETWORK_ADDRESS))) {
        this.setNetworkAddress(null);
      } else {
        try {
          this.setNetworkAddress(
              InetAddress.getByName(inputSettings.get(PlatformSmartmeteringKeys.NETWORK_ADDRESS)));
        } catch (final UnknownHostException e) {
          LOGGER.error("Exception occured while setting InetAddress for device.", e);
        }
      }
    }

    return (T) this;
  }
}
