//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
