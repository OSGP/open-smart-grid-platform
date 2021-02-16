/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class ActualPowerQualityRequest implements Serializable, ActionRequest {

    private static final long serialVersionUID = 7924053476264448032L;
    
    private final String profileType;

    public ActualPowerQualityRequest(final String profileType) {
        this.profileType = profileType;
    }

    public String getProfileType() {
        return this.profileType;
    }

    @Override
    public void validate() throws FunctionalException {
        // not needed here
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.GET_ACTUAL_POWER_QUALITY;
    }

    @Override
    public String toString() {
        return String.format("%s[profileType=%s]",
                ActualPowerQualityRequest.class.getSimpleName(), this.profileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.profileType);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActualPowerQualityRequest)) {
            return false;
        }
        final ActualPowerQualityRequest other = (ActualPowerQualityRequest) obj;
        return Objects.equals(this.profileType, other.profileType);
    }
}
