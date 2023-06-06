// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;

public class PushObjectConverter extends BidirectionalConverter<CosemObjectDefinition, PushObject> {

  @Override
  public PushObject convertTo(
      final CosemObjectDefinition source,
      final Type<PushObject> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final ObisCodeValues obisCodeValues =
        this.mapperFacade.map(source.getLogicalName(), ObisCodeValues.class);

    final PushObject convertedPushObject = new PushObject();

    convertedPushObject.setClassId(source.getClassId());
    convertedPushObject.setLogicalName(obisCodeValues);
    convertedPushObject.setAttributeIndex((byte) source.getAttributeIndex());
    convertedPushObject.setDataIndex(source.getDataIndex());

    return convertedPushObject;
  }

  @Override
  public CosemObjectDefinition convertFrom(
      final PushObject source,
      final Type<CosemObjectDefinition> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final CosemObisCode obisCode =
        this.mapperFacade.map(source.getLogicalName(), CosemObisCode.class);

    return new CosemObjectDefinition(
        source.getClassId(), obisCode, source.getAttributeIndex(), source.getDataIndex());
  }
}
