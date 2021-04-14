/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870.factory;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeShortFloat;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.iec60870.exceptions.InformationObjectTypeNotSupportedException;
import org.springframework.stereotype.Component;

@Component
public class InformationElementFactory {

  private static Map<Iec60870InformationObjectType, Function<Object, InformationElement[][]>>
      factoryMap = new EnumMap<>(Iec60870InformationObjectType.class);

  static {
    factoryMap.put(
        Iec60870InformationObjectType.QUALIFIER_OF_INTERROGATION,
        InformationElementFactory::createQualityOfInterrogationInformationElement);
    factoryMap.put(
        Iec60870InformationObjectType.SHORT_FLOAT, InformationElementFactory::createShortFloat);
    factoryMap.put(
        Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY,
        InformationElementFactory::createSinglePointInformationWithQuality);
    factoryMap.put(
        Iec60870InformationObjectType.TIME56, InformationElementFactory::createTimestamp56);
  }

  public InformationElement[][] createInformationElements(
      final Iec60870InformationObjectType informationObjectType, final Object value) {

    if (factoryMap.containsKey(informationObjectType)) {
      return factoryMap.get(informationObjectType).apply(value);
    } else {
      throw new InformationObjectTypeNotSupportedException(
          informationObjectType + " is not supported yet");
    }
  }

  private static InformationElement[][] createQualityOfInterrogationInformationElement(
      final Object value) {
    return new InformationElement[][] {{new IeQualifierOfInterrogation((Integer) value)}};
  }

  private static InformationElement[][] createShortFloat(final Object value) {
    return new InformationElement[][] {
      {new IeShortFloat((Float) value), new IeQuality(false, false, false, false, false)}
    };
  }

  private static InformationElement[][] createSinglePointInformationWithQuality(
      final Object value) {
    return new InformationElement[][] {
      {new IeSinglePointWithQuality((Boolean) value, false, false, false, false)}
    };
  }

  private static InformationElement[][] createTimestamp56(final Object value) {
    return new InformationElement[][] {
      {new IeTime56(((ZonedDateTime) value).toInstant().toEpochMilli())}
    };
  }
}
