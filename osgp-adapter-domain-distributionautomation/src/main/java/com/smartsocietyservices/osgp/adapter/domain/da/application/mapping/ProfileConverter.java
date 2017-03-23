/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.application.mapping;

import com.smartsocietyservices.osgp.domain.da.valueobjects.Profile;
import com.smartsocietyservices.osgp.domain.da.valueobjects.ProfileEntry;
import com.smartsocietyservices.osgp.dto.da.ProfileDto;
import com.smartsocietyservices.osgp.dto.da.ProfileEntryDto;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.List;

public class ProfileConverter extends BidirectionalConverter<Profile, ProfileDto> {

    @Override
    public ProfileDto convertTo(final Profile source, final Type<ProfileDto> destinationType) {
        final List<ProfileEntryDto> profileEntries = this.mapperFacade.mapAsList(source.getProfileEntries(),
                ProfileEntryDto.class);

        return new ProfileDto(source.getId(), source.getNode(), profileEntries);
    }

    @Override
    public Profile convertFrom(final ProfileDto source, final Type<Profile> destinationType) {
        final List<ProfileEntry> profileEntries = this.mapperFacade.mapAsList(source.getProfileEntries(),
                ProfileEntry.class);

        return new Profile(source.getId(), source.getNode(), profileEntries);
    }

}
