package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsExtendedRegister extends DlmsRegister {

    public DlmsExtendedRegister(final DlmsObjectType type, final String obisCode, final int scaler,
            final RegisterUnit unit, final Medium medium) {
        super(type, 4, obisCode, scaler, unit, medium);
    }
}
