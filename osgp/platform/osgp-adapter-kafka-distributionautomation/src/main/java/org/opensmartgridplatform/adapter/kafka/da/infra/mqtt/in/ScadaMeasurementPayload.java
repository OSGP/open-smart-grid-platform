/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.mqtt.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScadaMeasurementPayload {
    @JsonProperty("gisnr")
    private String substationIdentification;
    private String substationName;
    private String feeder;
    private String bayIdentification;
    @JsonProperty("D")
    private String date;
    @JsonProperty("uts")
    private long createdUtcSeconds;
    private String[] data;

}
