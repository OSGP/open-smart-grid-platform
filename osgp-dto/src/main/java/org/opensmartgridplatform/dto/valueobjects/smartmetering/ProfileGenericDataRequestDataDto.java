/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * request for ProfileGenericData
 */
public class ProfileGenericDataRequestDataDto implements ActionRequestDto {

    protected final ObisCodeValuesDto obisCode;
    protected final Date beginDate;
    protected final Date endDate;
    protected final ArrayList<CaptureObjectDefinitionDto> selectedValues = new ArrayList<>();

    private static final long serialVersionUID = -2483665562035897062L;

    public ProfileGenericDataRequestDataDto(final ObisCodeValuesDto obisCode, final Date beginDate, final Date endDate,
            final List<CaptureObjectDefinitionDto> selectedValues) {
        this.obisCode = obisCode;
        this.beginDate = new Date(beginDate.getTime());
        this.endDate = new Date(endDate.getTime());
        if (selectedValues != null) {
            this.selectedValues.addAll(selectedValues);
        }
    }

    public ProfileGenericDataRequestDataDto(final ObisCodeValuesDto obisCode, final Date beginDate,
            final Date endDate) {
        this(obisCode, beginDate, endDate, Collections.emptyList());
    }

    @Override
    public String toString() {
        return String.format("%s[obisCode=%s, begin=%tF %<tT.%<tL %<tZ, end=%tF %<tT.%<tL %<tZ, selected=%s]",
                ProfileGenericDataRequestDataDto.class.getSimpleName(), this.obisCode, this.beginDate, this.endDate,
                this.selectedValues.isEmpty() ? "all capture objects" : this.selectedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.obisCode, this.beginDate, this.endDate, this.selectedValues);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProfileGenericDataRequestDataDto)) {
            return false;
        }
        final ProfileGenericDataRequestDataDto other = (ProfileGenericDataRequestDataDto) obj;
        return Objects.equals(this.obisCode, other.obisCode) && Objects.equals(this.beginDate, other.beginDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.selectedValues, other.selectedValues);
    }

    public ObisCodeValuesDto getObisCode() {
        return this.obisCode;
    }

    public Date getBeginDate() {
        return new Date(this.beginDate.getTime());
    }

    public Date getEndDate() {
        return new Date(this.endDate.getTime());
    }

    public List<CaptureObjectDefinitionDto> getSelectedValues() {
        return new ArrayList<>(this.selectedValues);
    }
}
