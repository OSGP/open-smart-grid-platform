// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.cucumber.platform.core.builders.CucumberBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;

public class DlmsDeviceBuilder implements CucumberBuilder<DlmsDevice> {

  private String deviceIdentification = PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION;
  private Long version = PlatformSmartmeteringDefaults.VERSION;
  private String iccId = PlatformSmartmeteringDefaults.ICC_ID;
  private String communicationProvider = PlatformSmartmeteringDefaults.COMMUNICATION_PROVIDER;
  private String communicationMethod = PlatformSmartmeteringDefaults.COMMUNICATION_METHOD;
  private boolean hls3active = PlatformSmartmeteringDefaults.HLS3ACTIVE;
  private boolean hls4active = PlatformSmartmeteringDefaults.HLS4ACTIVE;
  private boolean hls5active = PlatformSmartmeteringDefaults.HLS5ACTIVE;
  private boolean lls1Active = PlatformSmartmeteringDefaults.LLS1_ACTIVE;
  private boolean useSn = PlatformSmartmeteringDefaults.USE_SN;
  private boolean useHdlc = PlatformSmartmeteringDefaults.USE_HDLC;
  private boolean polyphase = PlatformSmartmeteringDefaults.POLYPHASE;
  private Integer challengeLength = PlatformSmartmeteringDefaults.CHALLENGE_LENGTH;
  private boolean withListSupported = PlatformSmartmeteringDefaults.WITH_LIST_SUPPORTED;
  private boolean selectiveAccessSupported =
      PlatformSmartmeteringDefaults.SELECTIVE_ACCESS_SUPPORTED;
  private boolean ipAddressIsStatic = PlatformSmartmeteringDefaults.IP_ADDRESS_IS_STATIC;
  private Long port;
  private Long clientId = PlatformSmartmeteringDefaults.CLIENT_ID;
  private Long logicalId = PlatformSmartmeteringDefaults.LOGICAL_ID;
  private boolean inDebugMode = PlatformSmartmeteringDefaults.IN_DEBUG_MODE;
  private String mbusIdentificationNumber = null;
  private String mbusManufacturerIdentification = null;
  private String protocolName = PlatformSmartmeteringDefaults.PROTOCOL;
  private String protocolVersion = PlatformSmartmeteringDefaults.PROTOCOL_VERSION;
  private Long invocationCounter = PlatformSmartmeteringDefaults.INVOCATION_COUNTER;

  private String timezone;

  public DlmsDeviceBuilder setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return this;
  }

  public DlmsDeviceBuilder setVersion(final Long version) {
    this.version = version;
    return this;
  }

  public DlmsDeviceBuilder setIccId(final String iccId) {
    this.iccId = iccId;
    return this;
  }

  public DlmsDeviceBuilder setCommunicationProvider(final String communicationProvider) {
    this.communicationProvider = communicationProvider;
    return this;
  }

  public DlmsDeviceBuilder setCommunicationMethod(final String communicationMethod) {
    this.communicationMethod = communicationMethod;
    return this;
  }

  public DlmsDeviceBuilder setHls3Active(final boolean hls3active) {
    this.hls3active = hls3active;
    return this;
  }

  public DlmsDeviceBuilder setHls4Active(final boolean hls4active) {
    this.hls4active = hls4active;
    return this;
  }

  public DlmsDeviceBuilder setHls5Active(final boolean hls5active) {
    this.hls5active = hls5active;
    return this;
  }

  public DlmsDeviceBuilder setLls1Active(final boolean lls1Active) {
    this.lls1Active = lls1Active;
    return this;
  }

  public DlmsDeviceBuilder setUseSn(final boolean useSn) {
    this.useSn = useSn;
    return this;
  }

  public DlmsDeviceBuilder setUseHdlc(final boolean useHdlc) {
    this.useHdlc = useHdlc;
    return this;
  }

  public DlmsDeviceBuilder setPolyphase(final boolean polyphase) {
    this.polyphase = polyphase;
    return this;
  }

  public DlmsDeviceBuilder setChallengeLength(final Integer challengeLength) {
    this.challengeLength = challengeLength;
    return this;
  }

  public DlmsDeviceBuilder setWithListSupported(final boolean withListSupported) {
    this.withListSupported = withListSupported;
    return this;
  }

  public DlmsDeviceBuilder setSelectiveAccessSupported(final boolean selectiveAccessSupported) {
    this.selectiveAccessSupported = selectiveAccessSupported;
    return this;
  }

  public DlmsDeviceBuilder setIpAddressIsStatic(final boolean ipAddressIsStatic) {
    this.ipAddressIsStatic = ipAddressIsStatic;
    return this;
  }

  public DlmsDeviceBuilder setPort(final Long port) {
    this.port = port;
    return this;
  }

  public DlmsDeviceBuilder setClientId(final Long clientId) {
    this.clientId = clientId;
    return this;
  }

  public DlmsDeviceBuilder setLogicalId(final Long logicalId) {
    this.logicalId = logicalId;
    return this;
  }

  public DlmsDeviceBuilder setInDebugMode(final boolean inDebugMode) {
    this.inDebugMode = inDebugMode;
    return this;
  }

  public DlmsDeviceBuilder setMbusIdentificationNumber(final String value) {
    this.mbusIdentificationNumber = value;
    return this;
  }

  public DlmsDeviceBuilder setMbusManufacturerIdentification(final String value) {
    this.mbusManufacturerIdentification = value;
    return this;
  }

  public DlmsDeviceBuilder setProtocolName(final ProtocolInfo protocolInfo) {
    this.protocolName = protocolInfo.getProtocol();
    this.protocolVersion = protocolInfo.getProtocolVersion();
    return this;
  }

  public DlmsDeviceBuilder setInvocationCounter(final Long invocationCounter) {
    this.invocationCounter = invocationCounter;
    return this;
  }

  @Override
  public DlmsDeviceBuilder withSettings(final Map<String, String> inputSettings) {
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION)) {
      this.setDeviceIdentification(
          (inputSettings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.VERSION)) {
      this.setVersion(Long.parseLong(inputSettings.get(PlatformSmartmeteringKeys.VERSION)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.ICC_ID)) {
      this.setIccId((inputSettings.get(PlatformSmartmeteringKeys.ICC_ID)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.COMMUNICATION_PROVIDER)) {
      this.setCommunicationProvider(
          (inputSettings.get(PlatformSmartmeteringKeys.COMMUNICATION_PROVIDER)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.COMMUNICATION_METHOD)) {
      this.setCommunicationMethod(
          (inputSettings.get(PlatformSmartmeteringKeys.COMMUNICATION_METHOD)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.HLS3ACTIVE)) {
      this.setHls3Active(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.HLS3ACTIVE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.HLS4ACTIVE)) {
      this.setHls4Active(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.HLS4ACTIVE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.HLS5ACTIVE)) {
      this.setHls5Active(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.HLS5ACTIVE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.LLS1_ACTIVE)) {
      this.setLls1Active(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.LLS1_ACTIVE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.USE_HDLC)) {
      this.setUseHdlc(Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.USE_HDLC)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.USE_SN)) {
      this.setUseSn(Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.USE_SN)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.POLYPHASE)) {
      this.setPolyphase(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.POLYPHASE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CHALLENGE_LENGTH)) {
      this.setChallengeLength(
          Integer.parseInt(inputSettings.get(PlatformSmartmeteringKeys.CHALLENGE_LENGTH)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.WITH_LIST_SUPPORTED)) {
      this.setWithListSupported(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.WITH_LIST_SUPPORTED)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED)) {
      this.setSelectiveAccessSupported(
          Boolean.parseBoolean(
              inputSettings.get(PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC)) {
      this.setIpAddressIsStatic(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.CLIENT_ID)) {
      this.setClientId(Long.parseLong(inputSettings.get(PlatformSmartmeteringKeys.CLIENT_ID)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.IN_DEBUG_MODE)) {
      this.setInDebugMode(
          Boolean.parseBoolean(inputSettings.get(PlatformSmartmeteringKeys.IN_DEBUG_MODE)));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER)) {
      this.setMbusIdentificationNumber(
          inputSettings.get(PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION)) {
      this.setMbusManufacturerIdentification(
          inputSettings.get(PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.INVOCATION_COUNTER)) {
      this.setInvocationCounter(
          getLong(inputSettings, PlatformSmartmeteringKeys.INVOCATION_COUNTER));
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_TIMEZONE)) {
      this.setTimezone(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_TIMEZONE));
    }

    /**
     * For port/logical_id we want to be able to override the default value to be null to enable
     * testing against a real device.
     */
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.LOGICAL_ID)) {
      if (inputSettings.get(PlatformSmartmeteringKeys.LOGICAL_ID).isEmpty()) {
        this.setLogicalId(null);
      } else {
        this.setLogicalId(Long.parseLong(inputSettings.get(PlatformSmartmeteringKeys.LOGICAL_ID)));
      }
    }
    if (inputSettings.containsKey(PlatformSmartmeteringKeys.PORT)) {
      if (inputSettings.get(PlatformSmartmeteringKeys.PORT).isEmpty()) {
        this.setPort(null);
      } else {
        this.setPort(Long.parseLong(inputSettings.get(PlatformSmartmeteringKeys.PORT)));
      }
    } else {
      this.setPort(this.getPortBasedOnProtocolInfo(this.protocolName, this.protocolVersion));
    }

    return this;
  }

  private void setTimezone(final String timezone) {
    this.timezone = timezone;
  }

  @Override
  public DlmsDevice build() {
    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification(this.deviceIdentification);
    dlmsDevice.setVersion(this.version);
    dlmsDevice.setIccId(this.iccId);
    dlmsDevice.setCommunicationProvider(this.communicationProvider);
    dlmsDevice.setCommunicationMethod(this.communicationMethod);
    dlmsDevice.setHls3Active(this.hls3active);
    dlmsDevice.setHls4Active(this.hls4active);
    dlmsDevice.setHls5Active(this.hls5active);
    dlmsDevice.setLls1Active(this.lls1Active);
    dlmsDevice.setUseHdlc(this.useHdlc);
    dlmsDevice.setUseSn(this.useSn);
    dlmsDevice.setPolyphase(this.polyphase);
    dlmsDevice.setChallengeLength(this.challengeLength);
    dlmsDevice.setWithListSupported(this.withListSupported);
    dlmsDevice.setSelectiveAccessSupported(this.selectiveAccessSupported);
    dlmsDevice.setIpAddressIsStatic(this.ipAddressIsStatic);
    dlmsDevice.setPort(this.port);
    dlmsDevice.setClientId(this.clientId);
    dlmsDevice.setLogicalId(this.logicalId);
    dlmsDevice.setInDebugMode(this.inDebugMode);
    dlmsDevice.setMbusIdentificationNumber(this.mbusIdentificationNumber);
    dlmsDevice.setMbusManufacturerIdentification(this.mbusManufacturerIdentification);
    dlmsDevice.setProtocol(this.protocolName, this.protocolVersion);
    dlmsDevice.setInvocationCounter(this.invocationCounter);
    dlmsDevice.setTimezone(this.timezone);

    return dlmsDevice;
  }

  public Long getPortBasedOnProtocolInfo(final String protocol, final String protocolVersion) {
    return PlatformSmartmeteringDefaults.PORT_MAPPING.keySet().stream()
        .filter(protocol1 -> this.protocolsAreEqual(protocol, protocolVersion, protocol1))
        .findFirst()
        .map(protocol2 -> PlatformSmartmeteringDefaults.PORT_MAPPING.get(protocol2))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format(
                        "No port mapped with protocol info [protocol %s and version %s]. "
                            + "Extended the existing port mapping in "
                            + "PlatformSmartmeteringDefaults.",
                        protocol, protocolVersion)));
  }

  private boolean protocolsAreEqual(
      final String protocol, final String protocolVersion, final ProtocolInfo protocolInfo) {
    return protocolInfo.getProtocol().equals(protocol)
        && protocolInfo.getProtocolVersion().equals(protocolVersion);
  }
}
