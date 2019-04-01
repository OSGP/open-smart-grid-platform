/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements;

public enum ReasonType {
    PERIODIC(1),
    BACKGROUND_SCAN(2),
    SPONTANEOUS(3);

    private int reasonCode;

    private ReasonType(final int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public int getReasonCode() {
        return this.reasonCode;
    }
}
