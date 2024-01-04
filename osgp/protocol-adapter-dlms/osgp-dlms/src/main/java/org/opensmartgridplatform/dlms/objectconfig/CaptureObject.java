// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import lombok.Getter;

@Getter
public class CaptureObject {
  private final CosemObject cosemObject;
  private final int attributeId;

  public CaptureObject(final CosemObject cosemObject, final int attributeId) {
    this.cosemObject = cosemObject;
    this.attributeId = attributeId;
  }

  public CaptureObject copy() {
    return new CaptureObject(this.cosemObject.copy(), this.attributeId);
  }

  public CaptureObject copyWithNewAttribute(final Attribute newAttribute) {
    return new CaptureObject(this.cosemObject.copyWithNewAttribute(newAttribute), this.attributeId);
  }
}
