package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfig {
    List<Protocol> protocols = new ArrayList<>();
    List<DlmsObject> objects = new ArrayList<>();

    public List<DlmsObject> getObjects() {
        return this.objects;
    }
}
