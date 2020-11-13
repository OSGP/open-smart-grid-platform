/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class GetOutagesRequestData implements Serializable, ActionRequest {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionValueObject
     * #validate()
     */
    @Override
    public void validate() throws FunctionalException {

    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.GET_OUTAGES;
    }
}
