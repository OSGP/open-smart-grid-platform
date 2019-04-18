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
