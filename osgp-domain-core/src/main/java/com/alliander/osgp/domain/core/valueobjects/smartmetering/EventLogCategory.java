package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public enum EventLogCategory implements Serializable {
    STANDARD_EVENT_LOG,
    FRAUD_DETECTION_LOG,
    COMMUNICATION_SESSION_LOG,
    M_BUS_EVENT_LOG
}
