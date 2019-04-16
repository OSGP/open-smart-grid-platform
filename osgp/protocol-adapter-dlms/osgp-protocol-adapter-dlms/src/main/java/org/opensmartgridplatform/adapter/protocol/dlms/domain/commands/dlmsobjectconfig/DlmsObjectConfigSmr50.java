package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.CLOCK;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfigSmr50 extends DlmsObjectConfig {

    public DlmsObjectConfigSmr50(final List<Protocol> similarProtocols) {

        this.protocols = new ArrayList<>();
        this.protocols.add(Protocol.SMR_5_0);
        this.protocols.addAll(similarProtocols);

        this.objects = createObjectListSMR50();
    }

    private static List<DlmsObject> createObjectListSMR50() {
        final List<DlmsObject> objectList = new ArrayList<>();

        // @formatter:off
        objectList.add(new DlmsClock(CLOCK, "0.0.1.0.0.255"));

        return objectList;
    }
}
