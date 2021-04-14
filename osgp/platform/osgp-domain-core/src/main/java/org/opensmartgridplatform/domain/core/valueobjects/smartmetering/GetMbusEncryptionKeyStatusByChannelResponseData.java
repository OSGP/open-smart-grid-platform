/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetMbusEncryptionKeyStatusByChannelResponseData extends ActionResponse
    implements Serializable {

  private static final long serialVersionUID = 3636769765482239443L;

  private final EncryptionKeyStatusType encryptionKeyStatus;
  private final short channel;
  private final String gatewayDeviceIdentification;

  public GetMbusEncryptionKeyStatusByChannelResponseData(
      final String gatewayDeviceIdentification,
      final EncryptionKeyStatusType encryptionKeyStatus,
      final short channel) {
    this.encryptionKeyStatus = encryptionKeyStatus;
    this.channel = channel;
    this.gatewayDeviceIdentification = gatewayDeviceIdentification;
  }

  public EncryptionKeyStatusType getEncryptionKeyStatus() {
    return this.encryptionKeyStatus;
  }

  public short getChannel() {
    return this.channel;
  }

  public String getGatewayDeviceIdentification() {
    return this.gatewayDeviceIdentification;
  }
}
