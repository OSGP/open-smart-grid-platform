/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjects;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntries;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileTypes;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityProfileData;

public class PowerQualityProfileResponseDataConverter
    extends CustomConverter<
        PowerQualityProfileData,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PowerQualityProfileData> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
          .PowerQualityProfileData
      convert(
          final PowerQualityProfileData source,
          final Type<
                  ? extends
                      org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                          .PowerQualityProfileData>
              destinationType,
          final MappingContext mappingContext) {

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PowerQualityProfileData
        result =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                .PowerQualityProfileData();
    result.setLogicalName(this.mapperFacade.map(source.getLogicalName(), ObisCodeValues.class));

    // TODO  result.setProfileTypes =   source.getProfileTypes();
    final ProfileTypes profileTypes = new ProfileTypes();
    profileTypes.getProfileType().addAll(this.mapProfileTypes(source));

    //    result.setProfileTypes(source.getProfileTypes());

    final CaptureObjects captureObjects = new CaptureObjects();
    captureObjects
        .getCaptureObjects()
        .addAll(this.mapperFacade.mapAsList(source.getCaptureObjects(), CaptureObject.class));
    result.setCaptureObjectList(captureObjects);

    final ProfileEntries profileEntries = new ProfileEntries();
    profileEntries.getProfileEntries().addAll(this.mapProfileEntries(source));
    result.setProfileEntryList(profileEntries);

    return result;
  }

  private List<ProfileEntry> mapProfileEntries(final PowerQualityProfileData source) {
    final List<ProfileEntry> result = new ArrayList<>();
    for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry
        profileEntryValuesVo : source.getProfileEntries()) {
      final ProfileEntry profileEntry = new ProfileEntry();

      for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue
          profileEntryValueVo : profileEntryValuesVo.getProfileEntryValues()) {
        profileEntry
            .getProfileEntryValue()
            .add(this.mapperFacade.map(profileEntryValueVo, ProfileEntryValue.class));
      }

      result.add(profileEntry);
    }
    return result;
  }

  private List<ProfileType> mapProfileTypes(final PowerQualityProfileData source) {
    final List<ProfileType> result = new ArrayList<>();
    //    for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileType
    //        profileTypeValuesVo : source.getProfileTypes()) {
    //      final ProfileType profileType = new ProfileType();
    //
    //      for (final
    // org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileTypeValue
    //          profileTypeValueValueVo : profileTypeValuesVo.getProfileTypeValues()) {
    //        profileType
    //            .
    //            .add(this.mapperFacade.map(profileEntryValueVo, ProfileEntryValue.class));
    //      }
    //
    //      result.add(profileType);
    //    }
    return result;
  }
}
