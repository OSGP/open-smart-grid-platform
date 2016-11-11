package com.alliander.osgp.adapter.domain.microgrids.application.mapping;

import java.util.List;

import com.alliander.osgp.domain.microgrids.valueobjects.Profile;
import com.alliander.osgp.domain.microgrids.valueobjects.SetDataSystemIdentifier;
import com.alliander.osgp.domain.microgrids.valueobjects.SetPoint;
import com.alliander.osgp.dto.valueobjects.microgrids.ProfileDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SetPointDto;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class SetDataSystemIdentifierConverter
        extends BidirectionalConverter<SetDataSystemIdentifier, SetDataSystemIdentifierDto> {

    @Override
    public SetDataSystemIdentifierDto convertTo(final SetDataSystemIdentifier source,
            final Type<SetDataSystemIdentifierDto> destinationType) {
        final List<SetPointDto> setPoints = this.mapperFacade.mapAsList(source.getSetPoints(), SetPointDto.class);
        final List<ProfileDto> profiles = this.mapperFacade.mapAsList(source.getProfiles(), ProfileDto.class);

        return new SetDataSystemIdentifierDto(source.getId(), source.getSystemType(), setPoints, profiles);
    }

    @Override
    public SetDataSystemIdentifier convertFrom(final SetDataSystemIdentifierDto source,
            final Type<SetDataSystemIdentifier> destinationType) {
        final List<SetPoint> setPoints = this.mapperFacade.mapAsList(source.getSetPoints(), SetPoint.class);
        final List<Profile> profiles = this.mapperFacade.mapAsList(source.getProfiles(), Profile.class);

        return new SetDataSystemIdentifier(source.getId(), source.getSystemType(), setPoints, profiles);
    }

}
