// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import java.util.List;
import lombok.Getter;

@Getter
public class ObjectListElement {
  private final int classId;
  private final int version;
  private final String logicalName;
  private final List<AttributeAccessItem> attributes;

  public ObjectListElement(
      final int classId,
      final int version,
      final String logicalName,
      final List<AttributeAccessItem> attributes) {
    this.classId = classId;
    this.version = version;
    this.logicalName = logicalName;
    this.attributes = attributes;
  }
}
