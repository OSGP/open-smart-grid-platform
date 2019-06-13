/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

public enum CaptureObjectDefinition {

    CLASS_ID(0), LOGICAL_NAME(1), ATTRIBUTE_INDEX(2), DATA_INDEX(3);

    private final int index;

    private CaptureObjectDefinition(final int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }
}
