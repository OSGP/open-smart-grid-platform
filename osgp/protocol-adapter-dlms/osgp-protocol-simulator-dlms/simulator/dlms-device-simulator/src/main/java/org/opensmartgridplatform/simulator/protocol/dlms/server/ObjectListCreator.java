/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.domain.smartmetering.config.Attribute;
import org.opensmartgridplatform.domain.smartmetering.config.CosemObject;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectService;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.Clock;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DoubleLongUnsignedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LongUnsignedData;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LongUnsignedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.OctetStringData;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ObjectListCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectListCreator.class);

  public List<CosemInterfaceObject> create(final DlmsObjectService dlmsObjectService) {
    final Map<DlmsObjectType, CosemObject> cosemObjectsMap =
        dlmsObjectService.getCosemObjects("SMR", "5.0.0");
    final List<CosemObject> inputList = new ArrayList<>(cosemObjectsMap.values());

    return inputList.stream()
        .map(this::convert)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private CosemInterfaceObject convert(final CosemObject cosemObject) {
    try {
      switch (cosemObject.classId) {
        case 1:
          return this.convertData(cosemObject);
        case 3:
          return this.convertRegister(cosemObject);
        case 8:
          return this.convertClock(cosemObject);
        default:
          throw new IllegalArgumentException("Unknown classId in profile: " + cosemObject.classId);
      }
    } catch (final Exception e) {
      LOGGER.error("Invalid object in profile: " + cosemObject.obis, e);
      return null;
    }
  }

  private CosemInterfaceObject convertData(final CosemObject cosemObject)
      throws IllegalArgumentException {
    final Attribute valueAttribute =
        cosemObject.getAttribute(RegisterAttribute.VALUE.attributeId());
    final String valueDataType = valueAttribute.getDatatype();

    switch (valueDataType) {
      case "long-unsigned":
        return new LongUnsignedData(cosemObject.obis, Integer.parseInt(valueAttribute.value));
      case "octet-string":
        return new OctetStringData(cosemObject.obis);
      default:
        throw new IllegalArgumentException(
            "Unknown datatype for data in profile: " + valueDataType);
    }
  }

  private CosemInterfaceObject convertRegister(final CosemObject cosemObject)
      throws IllegalArgumentException {
    final Attribute valueAttribute =
        cosemObject.getAttribute(RegisterAttribute.VALUE.attributeId());
    final String valueDataType = valueAttribute.getDatatype();
    final Attribute scalerUnitAttribute =
        cosemObject.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());
    final String scalerUnit = scalerUnitAttribute.getValue();

    switch (valueDataType) {
      case "double-long-unsigned":
        return new DoubleLongUnsignedRegister(
            cosemObject.obis,
            Long.parseLong(valueAttribute.value),
            this.getScaler(scalerUnit),
            this.getUnit(scalerUnit));
      case "long-unsigned":
        return new LongUnsignedRegister(
            cosemObject.obis,
            Integer.parseInt(valueAttribute.value),
            this.getScaler(scalerUnit),
            this.getUnit(scalerUnit));
      default:
        throw new IllegalArgumentException(
            "Unknown datatype for register in profile: " + valueDataType);
    }
  }

  private CosemInterfaceObject convertClock(final CosemObject cosemObject)
      throws IllegalArgumentException {
    final Attribute time = cosemObject.getAttribute(ClockAttribute.TIME.attributeId());
    if (time.getValue().equals("CURRENT_LOCAL_DATE_AND_TIME")) {
      return new Clock(LocalDateTime.now());
    } else {
      throw new IllegalArgumentException("Unknown value for clock in profile: " + time.getValue());
    }
  }

  private int getScaler(final String scaler_unit) {
    return Integer.parseInt(scaler_unit.split(",")[0]);
  }

  private UnitType getUnit(final String scaler_unit) throws IllegalArgumentException {
    final String unit = scaler_unit.split(",")[1].trim();

    switch (unit) {
      case "Wh":
        return UnitType.WATT_HOUR;
      case "V":
        return UnitType.VOLT;
      default:
        throw new IllegalArgumentException("Unknown unit in profile: " + unit);
    }
  }

  private List<CosemObject> createDummyList() {
    final List<CosemObject> objectListFromProfile = new ArrayList<>();

    objectListFromProfile.add(this.createDummyRegister("1.0.1.8.0.255", "10001"));
    objectListFromProfile.add(this.createDummyRegister("1.0.2.8.0.255", "20001"));
    objectListFromProfile.add(this.createDummyRegister("1.0.1.8.1.255", "10002"));
    objectListFromProfile.add(this.createDummyRegister("1.0.1.8.2.255", "10003"));
    objectListFromProfile.add(this.createDummyRegister("1.0.2.8.1.255", "20002"));
    objectListFromProfile.add(this.createDummyRegister("1.0.2.8.2.255", "20003"));
    objectListFromProfile.add(
        this.createDummyClock("0.0.1.0.0.255", "CURRENT_LOCAL_DATE_AND_TIME"));

    return objectListFromProfile;
  }

  private CosemObject createDummyRegister(final String obis, final String value) {
    final CosemObject newObject = new CosemObject();
    newObject.setClassId(3);
    newObject.setObis(obis);

    final ArrayList<Attribute> attributesObject = new ArrayList<>();
    attributesObject.add(this.createDummyAttribute(2, "double-long-unsigned", value));
    attributesObject.add(this.createDummyAttribute(3, "scal_unit_type", "0, Wh"));

    newObject.setAttributes(attributesObject);

    return newObject;
  }

  private CosemObject createDummyClock(final String obis, final String value) {
    final CosemObject newObject = new CosemObject();
    newObject.setClassId(8);
    newObject.setObis(obis);

    final ArrayList<Attribute> attributesObject = new ArrayList<>();
    attributesObject.add(this.createDummyAttribute(2, "octet-string", value));

    newObject.setAttributes(attributesObject);

    return newObject;
  }

  private Attribute createDummyAttribute(final int id, final String dataType, final String value) {
    final Attribute attribute = new Attribute();
    attribute.setId(id);
    attribute.setDatatype(dataType);
    attribute.setValue(value);

    return attribute;
  }
}
