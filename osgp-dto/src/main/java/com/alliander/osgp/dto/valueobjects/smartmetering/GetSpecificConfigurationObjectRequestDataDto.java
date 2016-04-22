/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class GetSpecificConfigurationObjectRequestDataDto implements ActionDto {

    private static final long serialVersionUID = -4509356465055101286L;

    private SpecificConfigurationObjectDto specificConfigurationObject;

    public GetSpecificConfigurationObjectRequestDataDto(SpecificConfigurationObjectDto specificConfigurationObject) {
        super();
        this.specificConfigurationObject = specificConfigurationObject;
    }

    public SpecificConfigurationObjectDto getSpecificConfigurationObject() {
        return specificConfigurationObject;
    }
    
}
