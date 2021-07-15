/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.logging;

import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.springframework.stereotype.Component;

@Component
public class DeviceLogItemBuilder {

  private String deviceIdentification = PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION;

  private String organisationIdentification = PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;

  private boolean incoming = true;

  private boolean valid = true;

  private String encoded = "encoded";

  private String decoded = "decoded";

  private int payloadMessageSerializedSize = 0;

  public DeviceLogItem build() {
    return new DeviceLogItem.Builder()
        .withOrganisationIdentification(this.organisationIdentification)
        .withDeviceUid(this.deviceIdentification)
        .withDeviceIdentification(this.deviceIdentification)
        .withIncoming(this.incoming)
        .withValid(this.valid)
        .withEncodedMessage(this.encoded)
        .withDecodedMessage(this.decoded)
        .withPayloadMessageSerializedSize(this.payloadMessageSerializedSize)
        .build();
  }

  public DeviceLogItemBuilder withDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return this;
  }

  public DeviceLogItemBuilder withOrganisationIdentification(
      final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
    return this;
  }

  public DeviceLogItemBuilder withIncoming(final boolean incoming) {
    this.incoming = incoming;
    return this;
  }

  public DeviceLogItemBuilder withValid(final boolean valid) {
    this.valid = valid;
    return this;
  }

  public DeviceLogItemBuilder withEncoded(final String encoded) {
    this.encoded = encoded;
    return this;
  }

  public DeviceLogItemBuilder withDecoded(final String decoded) {
    this.decoded = decoded;
    return this;
  }

  public DeviceLogItemBuilder withPayloadMessageSerializedSize(
      final int payloadMessageSerializedSize) {
    this.payloadMessageSerializedSize = payloadMessageSerializedSize;
    return this;
  }
}
