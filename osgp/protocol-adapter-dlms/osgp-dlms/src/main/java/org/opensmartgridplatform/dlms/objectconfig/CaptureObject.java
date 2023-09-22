// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import lombok.Data;

@Data
public class CaptureObject {
  private CosemObject cosemObject;
  private int attributeId;

  public CaptureObject(final CosemObject cosemObject, final int attributeId) {
    this.cosemObject = cosemObject;
    this.attributeId = attributeId;
  }
}
