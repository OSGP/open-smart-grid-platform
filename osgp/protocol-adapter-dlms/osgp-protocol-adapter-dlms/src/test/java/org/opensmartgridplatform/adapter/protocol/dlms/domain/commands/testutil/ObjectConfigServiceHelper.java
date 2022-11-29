/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import org.openmuc.jdlms.AttributeAddress;
import org.opensmartgridplatform.dlms.objectconfig.*;

public class ObjectConfigServiceHelper {
  private static final int ATTRIBUTE_ID_VALUE = 2;
  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;
  private static final String OBIS_CODE_CLOCK = "0.0.1.0.0.255";
  private static final int CLASS_ID_CLOCK = 8;

  public CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final String scalerUnitValue,
      final boolean polyphase) {
    final CosemObject object = new CosemObject();
    object.setClassId(classId);
    object.setObis(obis);
    object.setTag(tag);
    if (scalerUnitValue != null) {
      object.setAttributes(this.createScalerUnitAttributeList(scalerUnitValue));
    }
    object.setMeterTypes(this.getMeterTypes(polyphase));

    return object;
  }

  private List<Attribute> createScalerUnitAttributeList(final String value) {
    final Attribute scalerUnitAttribute = new Attribute();
    scalerUnitAttribute.setId(ATTRIBUTE_ID_SCALER_UNIT);
    scalerUnitAttribute.setValue(value);
    return Collections.singletonList(scalerUnitAttribute);
  }

  private List<MeterType> getMeterTypes(final boolean polyphase) {
    if (polyphase) {
      return Collections.singletonList(MeterType.PP);
    } else {
      return Arrays.asList(MeterType.PP, MeterType.SP);
    }
  }

  public EnumMap<ObjectProperty, List<Object>> getObjectProperties(
      final String profile, final PowerQualityRequest powerQualityRequest) {
    // Create map with the required properties and values for the power quality objects
    final EnumMap<ObjectProperty, List<Object>> pqProperties = new EnumMap<>(ObjectProperty.class);
    pqProperties.put(ObjectProperty.PQ_PROFILE, Collections.singletonList(profile));
    pqProperties.put(
        ObjectProperty.PQ_REQUEST,
        Arrays.asList(powerQualityRequest.name(), PowerQualityRequest.BOTH.name()));

    return pqProperties;
  }

  public List<AttributeAddress> getAttributeAddresses(final List<CosemObject> objects) {
    return objects.stream()
        .map(
            object ->
                new AttributeAddress(object.getClassId(), object.getObis(), ATTRIBUTE_ID_VALUE))
        .collect(Collectors.toList());
  }

  public static CosemObject getClockObject() {

    final CosemObject clock = new CosemObject();
    clock.setClassId(CLASS_ID_CLOCK);
    clock.setObis(OBIS_CODE_CLOCK);
    clock.setTag(DlmsObjectType.CLOCK.name());

    return clock;
  }
}
