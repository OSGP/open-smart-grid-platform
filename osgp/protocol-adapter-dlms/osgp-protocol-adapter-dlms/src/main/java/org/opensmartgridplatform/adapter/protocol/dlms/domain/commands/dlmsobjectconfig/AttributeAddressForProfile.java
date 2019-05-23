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
import java.util.stream.Collectors;

import org.openmuc.jdlms.AttributeAddress;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;

public class AttributeAddressForProfile {
    private final AttributeAddress attributeAddress;
    private final List<DlmsCaptureObject> selectedObjects;

    public AttributeAddressForProfile(AttributeAddress attributeAddress, List<DlmsCaptureObject> selectedObjects) {
        this.attributeAddress = attributeAddress;
        this.selectedObjects = selectedObjects;
    }

    public AttributeAddress getAttributeAddress() {
        return attributeAddress;
    }

    List<DlmsCaptureObject> getSelectedObjects() {
        return selectedObjects;
    }

    public Integer getIndex(final DlmsObjectType type, final Integer attributeId) {
        int index = 0;

        for (final DlmsCaptureObject object : selectedObjects) {
            if (object.getRelatedObject().getType().equals(type) && (attributeId == null
                    || object.getAttributeId() == attributeId)) {
                return index;
            }
            index++;
        }

        return null;
    }

    public DlmsCaptureObject getCaptureObject(DlmsObjectType dlmsObjectType) {
        return selectedObjects.stream()
                .filter(c -> c.getRelatedObject().getType() == dlmsObjectType)
                .collect(Collectors.toList())
                .get(0);
    }

    public List<DlmsRegister> getCaptureObjects(Class dlmsObjectClass, boolean defaultAttributeId) {
        return selectedObjects.stream()
                .filter(c -> !defaultAttributeId || c.getAttributeId() == c.getRelatedObject().getDefaultAttributeId())
                .map(DlmsCaptureObject::getRelatedObject)
                .filter(r -> dlmsObjectClass.isInstance(r))
                .map(r -> (DlmsRegister) r)
                .collect(Collectors.toList());
    }
}
