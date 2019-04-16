package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsData extends DlmsObject {

    public DlmsData(final DlmsObjectType type, final String obisCode) {
        super(type, 1, obisCode);
    }
}
