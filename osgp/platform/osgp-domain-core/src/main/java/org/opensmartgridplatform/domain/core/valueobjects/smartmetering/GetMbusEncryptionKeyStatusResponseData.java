/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
