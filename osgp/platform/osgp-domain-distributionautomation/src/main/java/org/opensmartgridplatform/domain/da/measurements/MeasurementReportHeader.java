/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements;

public class MeasurementReportHeader {

    private MeasurementType measurementType;
    private ReasonType reasonType;
    private int originatorAddress;
    private int commonAddress;

    private MeasurementReportHeader(final Builder builder) {
        this.measurementType = builder.measurementType;
        this.reasonType = builder.reasonType;
        this.originatorAddress = builder.originatorAddress;
        this.commonAddress = builder.commonAddress;
    }

    public MeasurementType getMeasurementType() {
        return this.measurementType;
    }

    public ReasonType getReasonType() {
        return this.reasonType;
    }

    public int getOriginatorAddress() {
        return this.originatorAddress;
    }

    public int getCommonAddress() {
        return this.commonAddress;
    }

    public static class Builder {
        private MeasurementType measurementType;
        private ReasonType reasonType;
        private int originatorAddress;
        private int commonAddress;

        public Builder withMeasurementType(final MeasurementType measurementType) {
            this.measurementType = measurementType;
            return this;
        }

        public Builder withReasonType(final ReasonType reasonType) {
            this.reasonType = reasonType;
            return this;
        }

        public Builder withOriginatorAddress(final int originatorAddress) {
            this.originatorAddress = originatorAddress;
            return this;
        }

        public Builder withCommonAddress(final int commonAddress) {
            this.commonAddress = commonAddress;
            return this;
        }

        public MeasurementReportHeader build() {
            return new MeasurementReportHeader(this);
        }
    }
}
