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
