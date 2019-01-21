/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DeviceMessageLog {

    private final String messageType;
    private final Map<String, String> readVariables = new HashMap<>();

    public DeviceMessageLog(
            /* final IED ied, final LogicalDevice logicalDevice, */ final String messageType) {
        // this.ied = ied;
        // this.logicalDevice = logicalDevice;
        this.messageType = messageType;

    }

    private void addReadVariable(final String variable, final String value) {
        this.readVariables.put(variable, value);
    }

    public String getMessage() {
        // String result = "LogicalDevice: " + this.ied.getDescription() +
        // this.logicalDevice.getDescription();
        String result = new String().concat(" messageType: ").concat(this.messageType).concat(" {\n");

        for (final Entry<String, String> entry : this.readVariables.entrySet()) {
            result = result.concat(entry.getKey()).concat(": ").concat(entry.getValue()).concat("\n");
        }

        result = result.concat(" }");
        return result;
    }

}
