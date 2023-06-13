// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetMbusEncryptionKeyStatusResponseData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 3636769765482239443L;

  private String mbusDeviceIdentification;
  private EncryptionKeyStatusType encryptionKeyStatus;

  public GetMbusEncryptionKeyStatusResponseData(
      final String mbusDeviceIdentification, final EncryptionKeyStatusType encryptionKeyStatus) {
    this.encryptionKeyStatus = encryptionKeyStatus;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public EncryptionKeyStatusType getEncryptionKeyStatus() {
    return this.encryptionKeyStatus;
  }
}
