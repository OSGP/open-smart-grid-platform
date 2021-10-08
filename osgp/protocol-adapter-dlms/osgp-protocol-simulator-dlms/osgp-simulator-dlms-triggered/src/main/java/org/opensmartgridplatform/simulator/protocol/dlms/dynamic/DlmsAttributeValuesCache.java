/* 
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.dynamic;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import org.openmuc.jdlms.ObisCode;

public class DlmsAttributeValuesCache {

  private final Map<ObisCode, ObjectNode> attributeValuesByObisCode = new HashMap<>();

  @Override
  public String toString() {
    return String.format("DlmsAttributeValuesCache[%s]", this.attributeValuesByObisCode);
  }

  public void clearAllValues() {
    synchronized (this.attributeValuesByObisCode) {
      this.attributeValuesByObisCode.clear();
    }
  }

  public ObjectNode getAttributeValues(final ObisCode obisCode) {
    synchronized (this.attributeValuesByObisCode) {
      return this.attributeValuesByObisCode.get(obisCode);
    }
  }

  public void storeAttributeValues(final ObisCode obisCode, final ObjectNode attributeValues) {
    synchronized (this.attributeValuesByObisCode) {
      ObjectNode newValue;
      if (this.attributeValuesByObisCode.containsKey(obisCode)) {
        newValue = this.attributeValuesByObisCode.get(obisCode);
        attributeValues
            .fields()
            .forEachRemaining(
                field -> {
                  newValue.set(field.getKey(), field.getValue());
                });
      } else {
        newValue = attributeValues;
      }

      this.attributeValuesByObisCode.put(obisCode, newValue);
    }
  }

  public ObjectNode getAttributeValue(final ObisCode obisCode, final int attributeId) {
    synchronized (this.attributeValuesByObisCode) {
      if (!this.attributeValuesByObisCode.containsKey(obisCode)) {
        return null;
      }
      return (ObjectNode)
          this.attributeValuesByObisCode.get(obisCode).get(String.valueOf(attributeId));
    }
  }

  public void storeAttributeValue(
      final ObisCode obisCode, final int attributeId, final ObjectNode attributeValue) {
    synchronized (this.attributeValuesByObisCode) {
      final ObjectNode attributeValues;
      if (this.attributeValuesByObisCode.containsKey(obisCode)) {
        attributeValues = this.attributeValuesByObisCode.get(obisCode);
      } else {
        final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        attributeValues = jsonNodeFactory.objectNode();
      }
      attributeValues.set(String.valueOf(attributeId), attributeValue);
      this.attributeValuesByObisCode.put(obisCode, attributeValues);
    }
  }
}
