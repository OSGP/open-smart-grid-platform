/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class CorrelationUidPerDevice {

    private final Map<String, LinkedList<String>> correlationUidQueuePerDevice = new HashMap<>();

    public void enqueu(final String deviceIdentification, final String correlationUid) {
        this.correlationUidQueuePerDevice.computeIfAbsent(deviceIdentification, key -> new LinkedList<>())
                .add(correlationUid);
    }

    public Optional<String> dequeu(final String deviceIdentification) {
        final String correlationUid = this.correlationUidQueuePerDevice
                .getOrDefault(deviceIdentification, new LinkedList<>())
                .poll();
        if (correlationUid == null) {
            return Optional.empty();
        }
        return Optional.of(correlationUid);
    }
}
