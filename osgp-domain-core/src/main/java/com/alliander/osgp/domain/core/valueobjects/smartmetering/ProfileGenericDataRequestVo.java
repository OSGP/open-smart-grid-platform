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

public class ProfileGenericDataRequestVo implements Serializable, ActionRequest {

    private static final long serialVersionUID = -6134172239685084920L;

    private final String deviceIdentification;
    private final ObisCodeValues obisCode;
    private final Date beginDate;
    private final Date endDate;

    public ProfileGenericDataRequestVo(ObisCodeValues obisCode, Date beginDate, Date endDate,
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
        int result = super.hashCode();
        result = prime * result + ((this.deviceIdentification == null) ? 0 : this.deviceIdentification.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProfileGenericDataRequestVo other = (ProfileGenericDataRequestVo) obj;
        if (this.deviceIdentification == null) {
            if (other.deviceIdentification != null) {
                return false;
            }
        } else if (!this.deviceIdentification.equals(other.deviceIdentification)) {
            return false;
        }
        return true;
    }

}
