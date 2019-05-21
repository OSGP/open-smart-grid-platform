/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsCaptureObject {
    private final DlmsObject relatedObject;
    private final int attributeId;

    public DlmsCaptureObject(final DlmsObject relatedObject, final int attributeId) {
        this.relatedObject = relatedObject;
        this.attributeId = attributeId;
    }

    public DlmsCaptureObject(final DlmsObject relatedObject) {
        this.relatedObject = relatedObject;
        this.attributeId = relatedObject.getDefaultAttributeId();
    }

    public DlmsObject getRelatedObject() {
        return this.relatedObject;
    }

    public int getAttributeId() {
        return this.attributeId;
    }
}
