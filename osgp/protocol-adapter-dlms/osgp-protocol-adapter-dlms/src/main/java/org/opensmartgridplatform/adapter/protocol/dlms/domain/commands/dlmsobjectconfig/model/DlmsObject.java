/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;

public class DlmsObject {
    private static final int DEFAULT_ATTRIBUTE_ID = 2;

    private final DlmsObjectType type;
    private final int classId;
    private final String obisCode;

    public DlmsObject(final DlmsObjectType type, final int classId, final String obisCode) {
        this.type = type;
        this.classId = classId;
        this.obisCode = obisCode;
    }

    public DlmsObjectType getType() {
        return this.type;
    }

    public int getClassId() {
        return this.classId;
    }

    public String getObisCode() {
        return this.obisCode;
    }

    public int getDefaultAttributeId() {
        return DEFAULT_ATTRIBUTE_ID;
    }

    public boolean mediumMatches(Medium medium) {
        return true;
    }
}
