//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequestData;

public class GetPowerQualityProfileRequestDataConverter
    extends CustomConverter<GetPowerQualityProfileRequest, GetPowerQualityProfileRequestData> {

  @Override
  public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
    return this.sourceType.isAssignableFrom(sourceType)
        && this.destinationType.equals(destinationType);
  }

  @Override
  public GetPowerQualityProfileRequestData convert(
      final GetPowerQualityProfileRequest source,
      final Type<? extends GetPowerQualityProfileRequestData> destinationType,
      final MappingContext context) {
    final GetPowerQualityProfileRequestData data =
        new GetPowerQualityProfileRequestData(
            source.getProfileType(),
            source.getBeginDate().toGregorianCalendar().getTime(),
            source.getEndDate().toGregorianCalendar().getTime());
    if (source.getSelectedValues() != null) {
      final List<CaptureObjectDefinition> captureObjectDefinitions =
          source.getSelectedValues().getCaptureObject();
      captureObjectDefinitions.forEach(cod -> data.getSelectedValues().add(this.convert(cod)));
    }
    return data;
  }

  private org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObjectDefinition
      convert(final CaptureObjectDefinition cod) {
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues
        obisCodeValues =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues(
                (byte) cod.getLogicalName().getA(), (byte) cod.getLogicalName().getB(),
                (byte) cod.getLogicalName().getC(), (byte) cod.getLogicalName().getD(),
                (byte) cod.getLogicalName().getE(), (byte) cod.getLogicalName().getF());
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObjectDefinition
        captureObjectDefinition =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .CaptureObjectDefinition(
                cod.getClassId(), obisCodeValues, cod.getAttributeIndex(), cod.getDataIndex());
    return captureObjectDefinition;
  }
}
