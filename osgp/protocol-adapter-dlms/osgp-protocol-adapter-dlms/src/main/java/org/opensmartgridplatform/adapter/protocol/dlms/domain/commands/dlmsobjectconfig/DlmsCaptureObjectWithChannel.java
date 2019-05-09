/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;

public class DlmsCaptureObjectWithChannel extends DlmsCaptureObject {
    private final int channel;

    public DlmsCaptureObjectWithChannel(final DlmsObject object, final int channel, final int attributeId) {
        super(object, attributeId);
        this.channel = channel;
    }

    public DlmsCaptureObjectWithChannel(final DlmsObject object, final int channel) {
        super(object, object.getDefaultAttributeId());
        this.channel = channel;
    }

    public int getChannel() {
        return this.channel;
    }
}
