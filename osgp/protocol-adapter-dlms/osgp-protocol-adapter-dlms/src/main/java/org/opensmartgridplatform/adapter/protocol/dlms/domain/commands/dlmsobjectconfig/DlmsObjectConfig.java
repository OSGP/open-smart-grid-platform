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

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfig {
    List<Protocol> protocols;
    List<DlmsObject> objects;

    public DlmsObjectConfig() {

    }

    public DlmsObjectConfig(final List<Protocol> protocols, final List<DlmsObject> objects) {
        this.protocols = protocols;
        this.objects = objects;
    }

    public List<DlmsObject> getObjects() {
        return this.objects;
    }
}
