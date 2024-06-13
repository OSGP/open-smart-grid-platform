// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform;

import java.util.HashMap;
import java.util.Map;
import org.junit.platform.commons.util.StringUtils;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractPlatformDeviceCreator<T extends Device>
    implements PlatformDeviceCreator<T> {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired private ProtocolInfoRepository protocolInfoRepository;

  @Autowired private DomainInfoRepository domainInfoRepository;

  // Allows for optional override of the default network address
  @Value("${device.networkaddress}")
  private String deviceNetworkAddress;

  protected String networkAddress(final Map<String, String> settings) {
    final var defaultNetworkAddress =
        StringUtils.isNotBlank(this.deviceNetworkAddress)
            ? this.deviceNetworkAddress
            : PlatformDefaults.DEFAULT_NETWORK_ADDRESS;
    return ReadSettingsHelper.getNullOrNonEmptyString(
        settings, PlatformKeys.KEY_NETWORKADDRESS, defaultNetworkAddress);
  }

  protected String deviceIdentification(final Map<String, String> settings) {
    return ReadSettingsHelper.getString(
        settings,
        PlatformKeys.KEY_DEVICE_IDENTIFICATION,
        PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
  }

  protected DeviceLifecycleStatus deviceLifecycleStatus(final Map<String, String> settings) {
    return ReadSettingsHelper.getEnum(
        settings,
        PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS,
        DeviceLifecycleStatus.class,
        PlatformDefaults.DEFAULT_DEVICE_LIFECYCLE_STATUS);
  }

  protected boolean activated(final Map<String, String> settings) {
    return ReadSettingsHelper.getBoolean(
        settings, PlatformKeys.KEY_ACTIVATED, PlatformDefaults.DEFAULT_ACTIVATED);
  }

  protected ProtocolInfo protocolInfo(final Protocol protocol) {
    return this.protocolInfoRepository.findByProtocolAndProtocolVersion(
        protocol.getProtocol(), protocol.getVersion());
  }

  protected DomainInfo domainInfo() {
    return this.domainInfoRepository.findByDomainAndDomainVersion("PUBLIC_LIGHTING", "1.0");
  }

  protected Device gatewayDevice(final Map<String, String> settings) {
    final var gatewayDeviceIdentification =
        ReadSettingsHelper.getString(settings, PlatformKeys.KEY_GATEWAY_DEVICE_ID, null);
    if (StringUtils.isNotBlank(gatewayDeviceIdentification)) {
      return this.deviceRepository.findByDeviceIdentification(gatewayDeviceIdentification);
    }
    return null;
  }

  private Organisation getOrganisation(final Map<String, String> settings) {
    final var organizationIdentification =
        ReadSettingsHelper.getString(
            settings,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    return this.organisationRepository.findByOrganisationIdentification(organizationIdentification);
  }

  protected DeviceAuthorization addDeviceAuthorization(final Device device) {
    return this.addDeviceAuthorization(device, new HashMap<>());
  }

  protected DeviceAuthorization addDeviceAuthorization(
      final Device device, final Map<String, String> settings) {
    return this.addDeviceAuthorization(device, this.getOrganisation(settings));
  }

  private DeviceAuthorization addDeviceAuthorization(
      final Device device, final Organisation organization) {
    final var deviceAuthorization =
        device.addAuthorization(organization, DeviceFunctionGroup.OWNER);
    return this.deviceAuthorizationRepository.save(deviceAuthorization);
  }
}
