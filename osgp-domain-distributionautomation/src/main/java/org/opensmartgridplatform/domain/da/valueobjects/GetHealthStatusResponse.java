/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.valueobjects;

import java.io.Serializable;

public class GetHealthStatusResponse implements Serializable {
    private String healthStatus;

    public GetHealthStatusResponse(final String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getHealthStatus() {
        return this.healthStatus;
    }
}
