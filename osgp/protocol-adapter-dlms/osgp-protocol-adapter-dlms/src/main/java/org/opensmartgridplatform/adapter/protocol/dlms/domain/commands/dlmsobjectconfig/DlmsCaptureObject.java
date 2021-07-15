/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

    public DlmsCaptureObject(final DlmsObject relatedObject, final int attributeId, int channel) {
        this.relatedObject = relatedObject;
        this.attributeId = attributeId;
        this.channel = channel;
    }

    public static DlmsCaptureObject create(DlmsObject relatedObject) {
        return new DlmsCaptureObject(relatedObject);
    }

    public static DlmsCaptureObject create(DlmsObject relatedObject, int attributeId) {
        return new DlmsCaptureObject(relatedObject, attributeId);
    }

    public static DlmsCaptureObject createWithChannel(DlmsObject relatedObject, int channel) {
        return new DlmsCaptureObject(relatedObject, relatedObject.getDefaultAttributeId(), channel);
    }

    public static DlmsCaptureObject createWithChannel(DlmsObject relatedObject, int channel, int attributeId) {
        return new DlmsCaptureObject(relatedObject, attributeId, channel);
    }

    public DlmsObject getRelatedObject() {
        return this.relatedObject;
    }

    public int getAttributeId() {
        return this.attributeId;
    }

    public boolean channelMatches(Integer channel) {
        return this.channel == null || this.channel.equals(channel);
    }
}
