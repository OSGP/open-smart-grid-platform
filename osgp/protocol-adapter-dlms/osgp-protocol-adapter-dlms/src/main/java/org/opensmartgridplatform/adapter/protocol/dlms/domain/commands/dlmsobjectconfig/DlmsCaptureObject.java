package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsCaptureObject {
    private final DlmsObject object;
    private final int attributeId;

    public DlmsCaptureObject(final DlmsObject object, final int attributeId) {
        this.object = object;
        this.attributeId = attributeId;
    }

    public DlmsCaptureObject(final DlmsObject object) {
        this.object = object;
        this.attributeId = object.getDefaultAttributeId();
    }

    public DlmsObject getObject() {
        return this.object;
    }

    public int getAttributeId() {
        return this.attributeId;
    }
}
