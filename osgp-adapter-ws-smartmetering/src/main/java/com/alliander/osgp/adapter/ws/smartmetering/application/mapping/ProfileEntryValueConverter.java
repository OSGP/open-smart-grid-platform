/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntryValue;

public class ProfileEntryValueConverter extends
CustomConverter<com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue, ProfileEntryValue> {

    @Override
    public ProfileEntryValue convert(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue source,
            final Type<? extends ProfileEntryValue> destinationType) {
        final ProfileEntryValue result = new ProfileEntryValue();
        result.getStringValueOrDateValueOrFloatValue().add(source.getValue());
        return result;
    }

}
