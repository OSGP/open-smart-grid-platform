/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DelayRequest {
    private final Integer delay;

    @JsonCreator
    public DelayRequest(@JsonProperty("delay") final Integer delay) {
        this.delay = delay;
    }

    public Integer getDelay() {
        return this.delay;
    }

    @Override
    public String toString() {
        return String.format("DelayRequest[delay=%s]", this.delay);
    }
}
