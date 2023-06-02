//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsClassVersion.VERSION_0;

import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsClassVersion;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsObject {
  private static final int DEFAULT_ATTRIBUTE_ID = 2;

  private final DlmsObjectType type;
  private final int classId;
  private final String obisCode;
  private final DlmsClassVersion version;

  public DlmsObject(final DlmsObjectType type, final int classId, final String obisCode) {
    this.type = type;
    this.classId = classId;
    this.obisCode = obisCode;
    this.version = VERSION_0;
  }

  public DlmsObject(
      final DlmsObjectType type,
      final int classId,
      final String obisCode,
      final DlmsClassVersion version) {
    this.type = type;
    this.classId = classId;
    this.obisCode = obisCode;
    this.version = version;
  }

  public DlmsObjectType getType() {
    return this.type;
  }

  public int getClassId() {
    return this.classId;
  }

  public String getObisCodeAsString() {
    return this.obisCode;
  }

  public ObisCode getObisCode() {
    return new ObisCode(this.obisCode);
  }

  public ObisCode getObisCodeWithChannel(final int channel) {
    final String obisCodeWithChannel = this.obisCode.replace("<c>", String.valueOf(channel));
    return new ObisCode(obisCodeWithChannel);
  }

  public DlmsClassVersion getVersion() {
    return this.version;
  }

  public int getDefaultAttributeId() {
    return DEFAULT_ATTRIBUTE_ID;
  }

  /**
   * @param medium Specifies the medium to match on in overriding classes
   */
  public boolean mediumMatches(final Medium medium) {
    return true;
  }
}
