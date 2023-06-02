//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
