/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.helpers;

public enum DeviceType {
    // SSLD("SSLD"),
    DISTRIBUTION_AUTOMATION_DEVICE("RTU"),
    LIGHT_MEASUREMENT_DEVICE("LMD"),
    LIGHT_MEASUREMENT_GATEWAY("LMG");

    private String type;

    DeviceType(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
