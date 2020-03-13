/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.mqtt.spec;

public class Message {

    private String topic;
    private String payload;
    private long pauseMillis;

    public String getTopic() {
        return this.topic;
    }

    public String getPayload() {
        return this.payload;
    }

    public long getPauseMillis() {
        return this.pauseMillis;
    }
}
