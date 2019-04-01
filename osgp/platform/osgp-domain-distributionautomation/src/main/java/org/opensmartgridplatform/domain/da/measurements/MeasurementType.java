/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.util.Arrays;

public enum MeasurementType {
    MEASURED_SHORT_FLOAT_WITH_TIME_TAG(36, "M_ME_TF_1");

    private int identifierNumber;
    private String identifierName;

    private MeasurementType(final int identifierNumber, final String identifierName) {
        this.identifierNumber = identifierNumber;
        this.identifierName = identifierName;
    }

    public int getIdentifierNumber() {
        return this.identifierNumber;
    }

    public String getIdentifierName() {
        return this.identifierName;
    }

    public static MeasurementType forIdentifierNumber(final int identifierNumber) {
        return Arrays.asList(values()).stream().filter(m -> m.identifierNumber == identifierNumber).findFirst().get();
    }

    public static MeasurementType forIdentifierName(final String identifierName) {
        return Arrays.asList(values()).stream().filter(m -> m.identifierName == identifierName).findFirst().get();
    }

}
