package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsRegister extends DlmsObject {

    private static final int SCALER_UNIT_ATTRIBUTE_ID = 3;

    private final int scaler;
    private final RegisterUnit unit;
    private final Medium medium;

    public DlmsRegister(final DlmsObjectType type, final String obisCode, final int scaler, final RegisterUnit unit,
            final Medium medium) {
        super(type, 3, obisCode);
        this.scaler = scaler;
        this.unit = unit;
        this.medium = medium;
    }

    DlmsRegister(final DlmsObjectType type, final int classId, final String obisCode, final int scaler,
            final RegisterUnit unit, final Medium medium) {
        super(type, classId, obisCode);
        this.scaler = scaler;
        this.unit = unit;
        this.medium = medium;
    }

    public int getScaler() {
        return this.scaler;
    }

    public RegisterUnit getUnit() {
        return this.unit;
    }

    public Medium getMedium() {
        return this.medium;
    }

    public int getScalerUnitAttributeId() {
        return SCALER_UNIT_ATTRIBUTE_ID;
    }
}
