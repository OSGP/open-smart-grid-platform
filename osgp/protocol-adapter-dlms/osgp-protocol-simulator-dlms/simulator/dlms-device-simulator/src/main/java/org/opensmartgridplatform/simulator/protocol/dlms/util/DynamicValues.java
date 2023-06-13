// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.rest.client.DlmsAttributeValuesClient;
import org.opensmartgridplatform.simulator.protocol.dlms.rest.client.DlmsAttributeValuesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicValues {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamicValues.class);

  private final DlmsAttributeValuesClient dlmsAttributeValuesClient;

  private final Map<Integer, Map<ObisCode, Map<Integer, DataObject>>> defaultAttributeValuesMap =
      new HashMap<>();

  public DynamicValues(final DlmsAttributeValuesClient dlmsAttributeValuesClient) {
    this.dlmsAttributeValuesClient = dlmsAttributeValuesClient;
  }

  public boolean hasDefaultAttributeValue(
      final CosemInterfaceObject cosemInterfaceObject, final Integer attributeId) {

    final Integer classId = cosemInterfaceObject.getClass().getAnnotation(CosemClass.class).id();
    final ObisCode obisCode = cosemInterfaceObject.getInstanceId();
    return this.hasDefaultAttributeValue(classId, obisCode, attributeId);
  }

  public boolean hasDefaultAttributeValue(
      final Integer classId, final ObisCode obisCode, final Integer attributeId) {
    return this.defaultAttributeValuesMap
        .getOrDefault(classId, Collections.emptyMap())
        .getOrDefault(obisCode, Collections.emptyMap())
        .containsKey(attributeId);
  }

  public DataObject getDefaultAttributeValue(
      final Integer classId, final ObisCode obisCode, final Integer attributeId) {

    final Map<ObisCode, Map<Integer, DataObject>> defaultAttributeValuesForClass =
        this.defaultAttributeValuesMap.getOrDefault(classId, Collections.emptyMap());
    final Map<Integer, DataObject> defaultAttributeValuesForObisCode =
        defaultAttributeValuesForClass.getOrDefault(obisCode, Collections.emptyMap());
    return defaultAttributeValuesForObisCode.get(attributeId);
  }

  public void setDefaultAttributeValue(
      final Integer classId,
      final ObisCode obisCode,
      final Integer attributeId,
      final DataObject attributeValue) {

    if (!this.defaultAttributeValuesMap.containsKey(classId)) {
      this.defaultAttributeValuesMap.put(classId, new HashMap<>());
    }
    final Map<ObisCode, Map<Integer, DataObject>> defaultAttributeValuesForClass =
        this.defaultAttributeValuesMap.get(classId);
    if (!defaultAttributeValuesForClass.containsKey(obisCode)) {
      defaultAttributeValuesForClass.put(obisCode, new HashMap<>());
    }
    final Map<Integer, DataObject> defaultAttributeValuesForObisCode =
        defaultAttributeValuesForClass.get(obisCode);
    defaultAttributeValuesForObisCode.put(attributeId, attributeValue);
  }

  public DataObject getDlmsAttributeValue(
      final CosemInterfaceObject cosemInterfaceObject, final Integer attributeId) {

    final int classId = cosemInterfaceObject.getClass().getAnnotation(CosemClass.class).id();
    final ObisCode obisCode = cosemInterfaceObject.getInstanceId();
    final DataObject result = this.getDlmsAttributeValue(classId, obisCode, attributeId);
    if (result != null) {
      return result;
    }
    return this.getDefaultAttributeValue(classId, obisCode, attributeId);
  }

  private DataObject getDlmsAttributeValue(
      final int classId, final ObisCode obisCode, final Integer attributeId) {
    try {
      return this.dlmsAttributeValuesClient.getDlmsAttributeValue(classId, obisCode, attributeId);
    } catch (final DlmsAttributeValuesClientException e) {
      LOGGER.error(
          "Error retrieving DLMS attribute value for class id {}, obis code {}, attribute id {}",
          classId,
          obisCode,
          attributeId,
          e);
      throw new WebApplicationException("Error accessing DLMS Attribute Values Service", e);
    }
  }

  /**
   * This methods sends a json request to the dlmsAttributeValuesClient, to store the given
   * attribute with the associated obiscode and attributeId, so that a next get request will
   * retrieve this value.
   */
  public void setDlmsAttributeValue(
      final CosemInterfaceObject cosemInterfaceObject,
      final Integer attributeId,
      final DataObject dataObject) {
    try {
      final int classId = cosemInterfaceObject.getClass().getAnnotation(CosemClass.class).id();
      final ObisCode obisCode = cosemInterfaceObject.getInstanceId();
      this.dlmsAttributeValuesClient.setDlmsAttributeValue(
          classId, obisCode, attributeId, dataObject);
    } catch (final DlmsAttributeValuesClientException e) {
      LOGGER.error("An error occured while setting a attribute on the simulator", e);
      throw new WebApplicationException("Error accessing DLMS Attribute Values Service", e);
    }
  }
}
