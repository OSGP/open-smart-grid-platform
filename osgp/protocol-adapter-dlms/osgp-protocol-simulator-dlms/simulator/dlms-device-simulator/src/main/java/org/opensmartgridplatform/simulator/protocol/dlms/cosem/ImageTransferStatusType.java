/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

public enum ImageTransferStatusType {
  IMAGE_TRANSFER_NOT_INITIATED(0),
  IMAGE_TRANSFER_INITIATED(1),
  IMAGE_VERIFICATION_INITIATED(2),
  IMAGE_VERIFICATION_SUCCESSFUL(3),
  IMAGE_VERIFICATION_FAILED(4),
  IMAGE_ACTIVATION_INITIATED(5),
  IMAGE_ACTIVATION_SUCCESSFUL(6),
  IMAGE_ACTIVATION_FAILED(7);

  int value;

  ImageTransferStatusType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
