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

public class DlmsRequestData {
    List<AttributeAddress> attributeAddresses;
    List<DlmsObject> selectedValues;

    public DlmsRequestData(final List<AttributeAddress> attributeAddresses, final List<DlmsObject> selectedValues) {
        this.attributeAddresses = attributeAddresses;
        this.selectedValues = selectedValues;
    }

    public List<AttributeAddress> getAttributeAddresses() {
        return this.attributeAddresses;
    }

    public List<DlmsObject> getSelectedValues() {
        return this.selectedValues;
    }
}
