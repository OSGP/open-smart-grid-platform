/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import org.joda.time.DateTime;

/**
 * request periodic reads for E or GAS meter
 *
 * @author dev
 */
public class ProfileGenericDataRequestDto implements ActionRequestDto {

    private final ObisCodeValuesDto obisCode;
    private final DateTime beginDate;
    private final DateTime endDate;

    private static final long serialVersionUID = -2483665562035897062L;

    public ProfileGenericDataRequestDto(ObisCodeValuesDto obisCode, DateTime beginDate, DateTime endDate) {
        super();
        this.obisCode = obisCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public ObisCodeValuesDto getObisCode() {
        return this.obisCode;
    }

    public DateTime getBeginDate() {
        return this.beginDate;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

}
