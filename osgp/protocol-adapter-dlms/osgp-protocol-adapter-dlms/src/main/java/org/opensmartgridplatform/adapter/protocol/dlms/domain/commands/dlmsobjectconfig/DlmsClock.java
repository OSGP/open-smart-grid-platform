package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsClock extends DlmsObject {

    public DlmsClock(final DlmsObjectType type, final String obisCode) {
        super(type, 8, obisCode);
    }
}
