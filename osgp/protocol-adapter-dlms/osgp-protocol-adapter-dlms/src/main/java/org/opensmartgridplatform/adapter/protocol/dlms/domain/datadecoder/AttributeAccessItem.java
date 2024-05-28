// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import lombok.Getter;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;

@Getter
public class AttributeAccessItem {
  private final int attributeId;
  private final AccessType accessMode;

  public AttributeAccessItem(final int attributeId, final AccessType accessMode) {
    this.attributeId = attributeId;
    this.accessMode = accessMode;
  }
}
