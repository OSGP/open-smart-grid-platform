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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.domain.smartmetering.config.Attribute;
import org.opensmartgridplatform.domain.smartmetering.config.CosemObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DoubleLongUnsignedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LongUnsignedData;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LongUnsignedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.OctetStringData;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectListCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectListCreator.class);

  // TODO: Get object list from profile config service instead of using dummy list
  public List<CosemInterfaceObject> create() {
    final List<CosemObject> inputList = this.createDummyList();

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

    final CosemObject object1 = new CosemObject();
    object1.setClassId(3);
    object1.setObis("1.0.1.8.0.255");

    final ArrayList<Attribute> attributesObject1 = new ArrayList<>();
    attributesObject1.add(this.createDummyAttribute(2, "double-long-unsigned", "100"));
    attributesObject1.add(this.createDummyAttribute(3, "scal_unit_type", "0, Wh"));

    object1.setAttributes(attributesObject1);

    objectListFromProfile.add(object1);

    final CosemObject object2 = new CosemObject();
    object2.setClassId(3);
    object2.setObis("1.0.1.8.1.255");

    final ArrayList<Attribute> attributesObject2 = new ArrayList<>();
    attributesObject2.add(this.createDummyAttribute(2, "double-long-unsigned", "101"));
    attributesObject2.add(this.createDummyAttribute(3, "scal_unit_type", "0, Wh"));

    object2.setAttributes(attributesObject2);

    objectListFromProfile.add(object2);

    return objectListFromProfile;
  }

  private Attribute createDummyAttribute(final int id, final String dataType, final String value) {
    final Attribute attribute = new Attribute();
    attribute.setId(id);
    attribute.setDatatype(dataType);
    attribute.setValue(value);

    return attribute;
  }
}
