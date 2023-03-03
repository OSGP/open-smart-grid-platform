/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityProfileData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;

public class GetPowerQualityProfileDtoConverter
    extends CustomConverter<GetPowerQualityProfileResponseDto, GetPowerQualityProfileResponse> {

  private final MapperFactory mapperFactory;

  public GetPowerQualityProfileDtoConverter(final MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }

  @Override
  public GetPowerQualityProfileResponse convert(
      final GetPowerQualityProfileResponseDto source,
      final Type<? extends GetPowerQualityProfileResponse> destinationType,
      final MappingContext mappingContext) {

    final GetPowerQualityProfileResponse response = new GetPowerQualityProfileResponse();

    final List<PowerQualityProfileData> powerQualityProfileDatas = new ArrayList<>();

    for (final PowerQualityProfileDataDto responseDataDto :
        source.getPowerQualityProfileResponseDatas()) {

      final ObisCodeValues obisCodeValues =
          this.mapperFactory
              .getMapperFacade()
              .map(responseDataDto.getLogicalName(), ObisCodeValues.class);

      final List<CaptureObject> captureObjects =
          new ArrayList<>(
              this.mapperFacade.mapAsList(
                  responseDataDto.getCaptureObjects(), CaptureObject.class));

      final List<ProfileEntry> profileEntries = this.makeProfileEntries(responseDataDto);

      final ProfileType profileType =
          this.mapperFactory
              .getMapperFacade()
              .map(responseDataDto.getProfileType(), ProfileType.class);

      powerQualityProfileDatas.add(
          new PowerQualityProfileData(obisCodeValues, captureObjects, profileEntries, profileType));
    }

    response.setPowerQualityProfileDatas(powerQualityProfileDatas);

    return response;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof GetPowerQualityProfileDtoConverter)) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    final GetPowerQualityProfileDtoConverter o = (GetPowerQualityProfileDtoConverter) other;
    if (this.mapperFactory.getMapperFacade() == null) {
      return o.mapperFactory.getMapperFacade() == null;
    }
    return this.mapperFactory
        .getMapperFacade()
        .getClass()
        .equals(o.mapperFactory.getMapperFacade().getClass());
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hashCode(this.mapperFactory.getMapperFacade());
  }

  private List<ProfileEntry> makeProfileEntries(final PowerQualityProfileDataDto responseDataDto) {

    final List<ProfileEntry> profileEntries = new ArrayList<>();

    for (final ProfileEntryDto profileEntryDto : responseDataDto.getProfileEntries()) {

      final List<ProfileEntryValue> profileEntryValues = new ArrayList<>();

      for (final ProfileEntryValueDto profileEntryValueDto :
          profileEntryDto.getProfileEntryValues()) {

        final ProfileEntryValue profileEntryValue =
            this.mapperFactory.getMapperFacade().map(profileEntryValueDto, ProfileEntryValue.class);
        profileEntryValues.add(profileEntryValue);
      }
      profileEntries.add(new ProfileEntry(profileEntryValues));
    }

    return profileEntries;
  }
}
