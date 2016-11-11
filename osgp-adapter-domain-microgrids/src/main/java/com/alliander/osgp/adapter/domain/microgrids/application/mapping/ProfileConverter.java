package com.alliander.osgp.adapter.domain.microgrids.application.mapping;

import java.util.List;

import com.alliander.osgp.domain.microgrids.valueobjects.Profile;
import com.alliander.osgp.domain.microgrids.valueobjects.ProfileEntry;
import com.alliander.osgp.dto.valueobjects.microgrids.ProfileDto;
import com.alliander.osgp.dto.valueobjects.microgrids.ProfileEntryDto;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

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
