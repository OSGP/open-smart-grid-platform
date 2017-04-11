/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class GetConfigurationObjectResponseConverter
        extends CustomConverter<GetConfigurationObjectResponseDto, GetConfigurationObjectResponse> {

    @Override
    public GetConfigurationObjectResponse convert(final GetConfigurationObjectResponseDto source,
            final Type<? extends GetConfigurationObjectResponse> destinationType) {

        return new GetConfigurationObjectResponse(
                this.mapperFacade.map(source.getConfigurationObjectDto(), ConfigurationObject.class));
    }

}
