// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;

public class DlmsCaptureObject {
  private final DlmsObject relatedObject;
  private final int attributeId;
  private Integer channel;

  public DlmsCaptureObject(final DlmsObject relatedObject) {
    this.relatedObject = relatedObject;
    this.attributeId = relatedObject.getDefaultAttributeId();
  }

  public DlmsCaptureObject(final DlmsObject relatedObject, final int attributeId) {
    this.relatedObject = relatedObject;
    this.attributeId = attributeId;
  }

  public DlmsCaptureObject(
      final DlmsObject relatedObject, final int attributeId, final int channel) {
    this.relatedObject = relatedObject;
    this.attributeId = attributeId;
    this.channel = channel;
  }

  public static DlmsCaptureObject create(final DlmsObject relatedObject) {
    return new DlmsCaptureObject(relatedObject);
  }

  public static DlmsCaptureObject create(final DlmsObject relatedObject, final int attributeId) {
    return new DlmsCaptureObject(relatedObject, attributeId);
  }

  public static DlmsCaptureObject createWithChannel(
      final DlmsObject relatedObject, final int channel) {
    return new DlmsCaptureObject(relatedObject, relatedObject.getDefaultAttributeId(), channel);
  }

  public static DlmsCaptureObject createWithChannel(
      final DlmsObject relatedObject, final int channel, final int attributeId) {
    return new DlmsCaptureObject(relatedObject, attributeId, channel);
  }

  public DlmsObject getRelatedObject() {
    return this.relatedObject;
  }

  public int getAttributeId() {
    return this.attributeId;
  }

  public boolean channelMatches(final Integer channel) {
    return this.channel == null || this.channel.equals(channel);
  }
}
