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

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class GetSpecificConfigurationObjectRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = 5319412997430962222L;

    private SpecificConfigurationObject specificConfigurationObject;

    public GetSpecificConfigurationObjectRequestData(final SpecificConfigurationObject specificConfigurationObject) {
        this.specificConfigurationObject = specificConfigurationObject;
    }

    public SpecificConfigurationObject getSpecificConfigurationObject() {
        return specificConfigurationObject;
    }

    @Override
    public void validate() throws FunctionalException {
        // 
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((specificConfigurationObject == null) ? 0 : specificConfigurationObject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GetSpecificConfigurationObjectRequestData other = (GetSpecificConfigurationObjectRequestData) obj;
        if (specificConfigurationObject == null) {
            if (other.specificConfigurationObject != null)
                return false;
        } else if (!specificConfigurationObject.equals(other.specificConfigurationObject))
            return false;
        return true;
    }
}


