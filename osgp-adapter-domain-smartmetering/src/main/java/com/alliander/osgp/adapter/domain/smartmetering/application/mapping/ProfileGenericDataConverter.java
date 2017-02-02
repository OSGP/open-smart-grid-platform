/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

public class ProfileGenericDataConverter extends
        CustomConverter<ProfileGenericDataResponseDto, ProfileGenericDataContainer> {

    @Override
    public ProfileGenericDataContainer convert(ProfileGenericDataResponseDto source,
            Type<? extends ProfileGenericDataContainer> destinationType) {
        return new ProfileGenericDataContainer(PeriodType.DAILY, null);
    }

}
