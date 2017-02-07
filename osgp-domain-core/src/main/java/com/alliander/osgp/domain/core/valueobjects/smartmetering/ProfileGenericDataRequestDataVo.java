/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class ProfileGenericDataRequestDataVo implements Serializable, ActionRequest {

    private static final long serialVersionUID = 2732162650167261497L;

    private final ObisCodeValues obisCode;
    private final DateTime beginDate;
    private final DateTime endDate;

    public ProfileGenericDataRequestDataVo(ObisCodeValues obisCode, DateTime beginDate, DateTime endDate) {
        super();
        this.obisCode = obisCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public ObisCodeValues getObisCode() {
        return this.obisCode;
    }

    public DateTime getBeginDate() {
        return this.beginDate;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

    @Override
    public void validate() throws FunctionalException {
        // not needed here
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.PROFILE_GENERIC_DATA;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.beginDate == null) ? 0 : this.beginDate.hashCode());
        result = prime * result + ((this.endDate == null) ? 0 : this.endDate.hashCode());
        result = prime * result + ((this.obisCode == null) ? 0 : this.obisCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProfileGenericDataRequestDataVo other = (ProfileGenericDataRequestDataVo) obj;
        if (this.beginDate == null) {
            if (other.beginDate != null) {
                return false;
            }
        } else if (!this.beginDate.equals(other.beginDate)) {
            return false;
        }
        if (this.endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!this.endDate.equals(other.endDate)) {
            return false;
        }
        if (this.obisCode == null) {
            if (other.obisCode != null) {
                return false;
            }
        } else if (!this.obisCode.equals(other.obisCode)) {
            return false;
        }
        return true;
    }

}
