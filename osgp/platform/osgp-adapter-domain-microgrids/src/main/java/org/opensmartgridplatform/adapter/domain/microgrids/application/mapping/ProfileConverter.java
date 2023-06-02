//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.microgrids.application.mapping;

import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.microgrids.valueobjects.Profile;
import org.opensmartgridplatform.domain.microgrids.valueobjects.ProfileEntry;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ProfileEntryDto;

public class ProfileConverter extends BidirectionalConverter<Profile, ProfileDto> {

  @Override
  public ProfileDto convertTo(
      final Profile source, final Type<ProfileDto> destinationType, final MappingContext context) {
    final List<ProfileEntryDto> profileEntries =
        this.mapperFacade.mapAsList(source.getProfileEntries(), ProfileEntryDto.class);

    return new ProfileDto(source.getId(), source.getNode(), profileEntries);
  }

  @Override
  public Profile convertFrom(
      final ProfileDto source, final Type<Profile> destinationType, final MappingContext context) {
    final List<ProfileEntry> profileEntries =
        this.mapperFacade.mapAsList(source.getProfileEntries(), ProfileEntry.class);

    return new Profile(source.getId(), source.getNode(), profileEntries);
  }
}
