/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Date;

/**
 * request for ProfileGenericData
 */
public class ProfileGenericDataRequestDto implements ActionRequestDto {

    private final String deviceIdentification;
    private final ObisCodeValuesDto obisCode;
    private final Date beginDate;
    private final Date endDate;

    private static final long serialVersionUID = -2483665562035897062L;

    public ProfileGenericDataRequestDto(final String deviceIdentification, final ObisCodeValuesDto obisCode,
            final Date beginDate, final Date endDate) {
        super();
        this.deviceIdentification = deviceIdentification;
        this.obisCode = obisCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public ObisCodeValuesDto getObisCode() {
        return this.obisCode;
    }

    public Date getBeginDate() {
        return this.beginDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

}
