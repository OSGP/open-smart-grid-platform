/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.List;
import java.util.stream.Stream;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public abstract class DlmsObjectConfig {

    private final List<Protocol> protocols;
    private final List<DlmsObject> objects;

    DlmsObjectConfig() {
        this.protocols = initProtocols();
        this.objects = initObjects();
    }

    public Stream<DlmsObject> getObjects() {
        return this.objects.stream();
    }

    boolean contains(Protocol protocol) {
        return protocols.contains(protocol);
    }

    abstract List<Protocol> initProtocols();

    abstract List<DlmsObject> initObjects();
}
