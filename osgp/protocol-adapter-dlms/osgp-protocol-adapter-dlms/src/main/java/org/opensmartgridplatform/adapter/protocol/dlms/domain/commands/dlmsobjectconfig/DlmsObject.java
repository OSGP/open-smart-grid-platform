package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

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
}
