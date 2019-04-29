/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;

public class AttributeAddressForProfile extends AttributeAddress {
    private final List<DlmsCaptureObject> selectedObjects;

    public AttributeAddressForProfile(final int classId, final ObisCode instanceId, final int attributeId,
            final SelectiveAccessDescription access, final List<DlmsCaptureObject> selectedObjects) {
        super(classId, instanceId, attributeId, access);
        this.selectedObjects = selectedObjects;
    }

    public List<DlmsCaptureObject> getSelectedObjects() {
        return this.selectedObjects;
    }
}
