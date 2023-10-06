// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import static org.opensmartgridplatform.shared.test.DefaultValue.notSet;
import static org.opensmartgridplatform.shared.test.DefaultValue.setTo;

import org.opensmartgridplatform.shared.test.DefaultValue;

/** Creates new instances, for testing purposes only. */
public class DlmsDeviceBuilder {
  private static long counter = 0L;

  private DefaultValue<String> deviceIdentification = notSet();
  private DefaultValue<Boolean> lls1Active = notSet();
  private DefaultValue<Boolean> hls3Active = notSet();
  private DefaultValue<Boolean> hls4Active = notSet();
  private DefaultValue<Boolean> hls5Active = notSet();
  private DefaultValue<String> protocol = notSet();
  private DefaultValue<String> configLookupType = notSet();
  private DefaultValue<Long> invocationCounter = notSet();
  private DefaultValue<String> ipAddress = notSet();
  private DefaultValue<Boolean> ipAddressStatic = notSet();
  private DefaultValue<String> iccId = notSet();
  private DefaultValue<String> communicationMethod = notSet();
  private DefaultValue<String> communicationProvider = notSet();
  private DefaultValue<Long> version = notSet();

  public DlmsDevice build() {
    counter += 1;
    final DlmsDevice device = new DlmsDevice();
    device.setDeviceIdentification(this.deviceIdentification.orElse("device-" + counter));
    device.setLls1Active(this.lls1Active.orElse(false));
    device.setHls3Active(this.hls3Active.orElse(false));
    device.setHls4Active(this.hls4Active.orElse(false));
    device.setHls5Active(this.hls5Active.orElse(false));
    device.setProtocol(this.protocol.orElse("protocol" + counter), "protocolVersion" + counter);
    device.setInvocationCounter(this.invocationCounter.orElse(100L + counter));
    device.setIpAddress(this.ipAddress.orElse(null));
    device.setIpAddressIsStatic(this.ipAddressStatic.orElse(true));
    device.setIccId(this.iccId.orElse(null));
    device.setCommunicationMethod(this.communicationMethod.orElse(null));
    device.setCommunicationProvider(this.communicationProvider.orElse(null));
    device.setVersion(this.version.orElse(0L));
    device.setConfigLookupType(this.configLookupType.orElse(null));
    return device;
  }

  /** set the configLookupType that will be used to lookup values in the object config service. */
  public DlmsDeviceBuilder withConfigLookupType(final String configLookupType) {
    this.configLookupType = setTo(configLookupType);
    return this;
  }

  public DlmsDeviceBuilder withDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = setTo(deviceIdentification);
    return this;
  }

  public DlmsDeviceBuilder withLls1Active(final boolean lls1Active) {
    this.lls1Active = setTo(lls1Active);
    return this;
  }

  public DlmsDeviceBuilder withHls3Active(final boolean hls3Active) {
    this.hls3Active = setTo(hls3Active);
    return this;
  }

  public DlmsDeviceBuilder withHls4Active(final boolean hls4Active) {
    this.hls4Active = setTo(hls4Active);
    return this;
  }

  public DlmsDeviceBuilder withHls5Active(final boolean hls5Active) {
    this.hls5Active = setTo(hls5Active);
    return this;
  }

  public DlmsDeviceBuilder withProtocol(final String protocol) {
    this.protocol = setTo(protocol);
    return this;
  }

  public DlmsDeviceBuilder withInvocationCounter(final Long invocationCounter) {
    this.invocationCounter = setTo(invocationCounter);
    return this;
  }

  public DlmsDeviceBuilder withIpAddress(final String ipAddress) {
    this.ipAddress = setTo(ipAddress);
    return this;
  }

  public DlmsDeviceBuilder withIpAddressStatic(final boolean ipAddressStatic) {
    this.ipAddressStatic = setTo(ipAddressStatic);
    return this;
  }

  public DlmsDeviceBuilder setIccId(final String iccId) {
    this.iccId = setTo(iccId);
    return this;
  }

  public DlmsDeviceBuilder withCommunicationMethod(final String communicationMethod) {
    this.communicationMethod = setTo(communicationMethod);
    return this;
  }

  public DlmsDeviceBuilder withCommunicationProvider(final String communicationProvider) {
    this.communicationProvider = setTo(communicationProvider);
    return this;
  }

  public DlmsDeviceBuilder withVersion(final Long version) {
    this.version = setTo(version);
    return this;
  }
}
