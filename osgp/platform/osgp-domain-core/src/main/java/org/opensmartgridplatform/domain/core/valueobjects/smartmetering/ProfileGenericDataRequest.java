/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class ProfileGenericDataRequest implements Serializable, ActionRequest {

    private static final long serialVersionUID = -6134172239685084920L;

    private final String deviceIdentification;
    private final ObisCodeValues obisCode;
    private final Date beginDate;
    private final Date endDate;
    private final List<CaptureObjectDefinition> selectedValues = new ArrayList<>();

    public ProfileGenericDataRequest(final ObisCodeValues obisCode, final Date beginDate, final Date endDate,
            final List<CaptureObjectDefinition> selectedValues, final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        this.obisCode = obisCode;
        this.beginDate = new Date(beginDate.getTime());
        this.endDate = new Date(endDate.getTime());
        if (selectedValues != null) {
            this.selectedValues.addAll(selectedValues);
        }
    }

    public ProfileGenericDataRequest(final ObisCodeValues obisCode, final Date beginDate, final Date endDate,
            final String deviceIdentification) {
        this(obisCode, beginDate, endDate, Collections.emptyList(), deviceIdentification);
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public ObisCodeValues getObisCode() {
        return this.obisCode;
    }

    public Date getBeginDate() {
        return new Date(this.beginDate.getTime());
    }

    public Date getEndDate() {
        return new Date(this.endDate.getTime());
    }

    public List<CaptureObjectDefinition> getSelectedValues() {
        return new ArrayList<>(this.selectedValues);
    }

    @Override
    public void validate() throws FunctionalException {
        // not needed here
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.GET_PROFILE_GENERIC_DATA;
    }

    @Override
    public String toString() {
        return String.format(
                "%s[device=%s, obisCode=%s, begin=%tF %<tT.%<tL %<tZ, end=%tF %<tT.%<tL %<tZ, selected=%s]",
                ProfileGenericDataRequest.class.getSimpleName(), this.deviceIdentification, this.obisCode,
                this.beginDate, this.endDate,
                this.selectedValues.isEmpty() ? "all capture objects" : this.selectedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deviceIdentification, this.obisCode, this.beginDate, this.endDate,
                this.selectedValues);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProfileGenericDataRequest)) {
            return false;
        }
        final ProfileGenericDataRequest other = (ProfileGenericDataRequest) obj;
        return Objects.equals(this.deviceIdentification, other.deviceIdentification)
                && Objects.equals(this.obisCode, other.obisCode) && Objects.equals(this.beginDate, other.beginDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.selectedValues, other.selectedValues);
    }
}
