// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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

    result.setProfileType(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileType.valueOf(
            source.getProfileType().toString()));

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
}
