/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MessageType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupAlarmConverter
    extends BidirectionalConverter<
        PushSetupAlarm,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
      convertTo(
          final PushSetupAlarm source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .PushSetupAlarm>
              destinationType,
          final MappingContext context) {
    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
        pushSetupAlarm =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .PushSetupAlarm();

    if (source.hasSendDestinationAndMethod()) {
      final SendDestinationAndMethod sendDestinationAndMethod =
          source.getSendDestinationAndMethod();
      final String destination = sendDestinationAndMethod.getDestination();
      if (!destination.matches("\\S++:\\d++")) {
        throw new IllegalArgumentException(
            "Unable to parse destination as \"<host>:<port>\": " + destination);
      }
      final String[] hostAndPort = destination.split(":");
      pushSetupAlarm.setHost(hostAndPort[0]);
      pushSetupAlarm.setPort(new BigInteger(hostAndPort[1]));
    }

    if (source.hasPushObjectList()) {
      final List<CosemObjectDefinition> sourcePushObjectList = source.getPushObjectList();
      for (final CosemObjectDefinition sourcePushObject : sourcePushObjectList) {

        final PushObject convertedPushObject = new PushObject();

        final ObisCodeValues convertedObisCode = new ObisCodeValues();
        final CosemObisCode sourceObisCode = sourcePushObject.getLogicalName();
        convertedObisCode.setA((short) sourceObisCode.getA());
        convertedObisCode.setB((short) sourceObisCode.getB());
        convertedObisCode.setC((short) sourceObisCode.getC());
        convertedObisCode.setD((short) sourceObisCode.getD());
        convertedObisCode.setE((short) sourceObisCode.getE());
        convertedObisCode.setF((short) sourceObisCode.getF());

        convertedPushObject.setClassId(sourcePushObject.getClassId());
        convertedPushObject.setLogicalName(convertedObisCode);
        convertedPushObject.setAttributeIndex((byte) sourcePushObject.getAttributeIndex());
        convertedPushObject.setDataIndex(sourcePushObject.getDataIndex());
        pushSetupAlarm.getPushObjectList().add(convertedPushObject);
      }
    }

    return pushSetupAlarm;
  }

  @Override
  public PushSetupAlarm convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
          source,
      final Type<PushSetupAlarm> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final PushSetupAlarm.Builder builder = new PushSetupAlarm.Builder();

    if (source.getHost() != null && source.getPort() != null) {
      final String destination = source.getHost() + ":" + source.getPort();
      final SendDestinationAndMethod sendDestinationAndMethod =
          new SendDestinationAndMethod(
              TransportServiceType.TCP, destination, MessageType.MANUFACTURER_SPECIFIC);
      builder.withSendDestinationAndMethod(sendDestinationAndMethod);
    }

    final List<PushObject> sourcePushObjects = source.getPushObjectList();

    if (sourcePushObjects != null) {
      final List<CosemObjectDefinition> convertedPushObjectList = new ArrayList<>();
      for (final PushObject sourcePushObject : sourcePushObjects) {
        final ObisCodeValues obisCode = sourcePushObject.getLogicalName();
        final CosemObisCode convertedObisCode =
            new CosemObisCode(
                obisCode.getA(),
                obisCode.getB(),
                obisCode.getC(),
                obisCode.getD(),
                obisCode.getE(),
                obisCode.getF());
        final CosemObjectDefinition convertedPushObject =
            new CosemObjectDefinition(
                sourcePushObject.getClassId(),
                convertedObisCode,
                sourcePushObject.getAttributeIndex(),
                sourcePushObject.getDataIndex());
        convertedPushObjectList.add(convertedPushObject);
      }
      builder.withPushObjectList(convertedPushObjectList);
    }

    return builder.build();
  }
}
