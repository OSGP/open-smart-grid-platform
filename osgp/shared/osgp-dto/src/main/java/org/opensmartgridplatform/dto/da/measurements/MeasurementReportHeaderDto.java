/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.da.measurements;

import java.io.Serializable;
import java.util.Objects;

// TODO - Replace the measurement report header by a more generic solution, probably a Map<String,String> could be used.
public class MeasurementReportHeaderDto implements Serializable {

    private static final long serialVersionUID = 1408641961558265027L;

    private final String typeIdentification;
    private final String reason;
    private final int commonAddress;
    private final int originatorAddress;

    public MeasurementReportHeaderDto(final String typeIdentification, final String reason, final int originatorAddress,
            final int commonAddress) {
        this.typeIdentification = typeIdentification;
        this.reason = reason;
        this.originatorAddress = originatorAddress;
        this.commonAddress = commonAddress;
    }

    public String getTypeIdentification() {
        return this.typeIdentification;
    }

    public String getReason() {
        return this.reason;
    }

    public int getCommonAddress() {
        return this.commonAddress;
    }

    public int getOriginatorAddress() {
        return this.originatorAddress;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MeasurementReportHeaderDto)) {
            return false;
        }
        final MeasurementReportHeaderDto that = (MeasurementReportHeaderDto) obj;
        return Objects.equals(this.typeIdentification, that.typeIdentification)
                && Objects.equals(this.reason, that.reason)
                && Objects.equals(this.originatorAddress, that.originatorAddress)
                && Objects.equals(this.commonAddress, that.commonAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.typeIdentification, this.reason, this.originatorAddress, this.commonAddress);
    }
}
