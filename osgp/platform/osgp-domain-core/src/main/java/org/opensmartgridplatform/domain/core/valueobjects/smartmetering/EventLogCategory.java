/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public enum EventLogCategory implements Serializable {
    STANDARD_EVENT_LOG,
    FRAUD_DETECTION_LOG,
    COMMUNICATION_SESSION_LOG,
    M_BUS_EVENT_LOG;

    public static EventLogCategory fromValue(final String v) {
        return valueOf(v);
    }

}

