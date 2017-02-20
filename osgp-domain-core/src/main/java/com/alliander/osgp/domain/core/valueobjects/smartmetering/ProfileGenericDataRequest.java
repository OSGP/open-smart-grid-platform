/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class ProfileGenericDataRequest implements Serializable, ActionRequest {

    private static final long serialVersionUID = -6134172239685084920L;

    private final String deviceIdentification;
    private final ObisCodeValues obisCode;
    private final Date beginDate;
    private final Date endDate;

    public ProfileGenericDataRequest(ObisCodeValues obisCode, Date beginDate, Date endDate,
            final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        this.obisCode = obisCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public ObisCodeValues getObisCode() {
        return this.obisCode;
    }

    public Date getBeginDate() {
        return this.beginDate;
    }

    public Date getEndDate() {
        return this.endDate;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.beginDate == null) ? 0 : this.beginDate.hashCode());
        result = prime * result + ((this.deviceIdentification == null) ? 0 : this.deviceIdentification.hashCode());
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
        ProfileGenericDataRequest other = (ProfileGenericDataRequest) obj;
        if (this.beginDate == null) {
            if (other.beginDate != null) {
                return false;
            }
        } else if (!this.beginDate.equals(other.beginDate)) {
            return false;
        }
        if (this.deviceIdentification == null) {
            if (other.deviceIdentification != null) {
                return false;
            }
        } else if (!this.deviceIdentification.equals(other.deviceIdentification)) {
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
