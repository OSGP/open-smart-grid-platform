/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ma.glasnost.orika.MapperFacade;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigureDefinableLoadProfileCommandExecutor
    extends AbstractCommandExecutor<DefinableLoadProfileConfigurationDto, Void> {

  private static final int CLASS_ID = InterfaceClass.PROFILE_GENERIC.id();
  private static final ObisCode LOGICAL_NAME = new ObisCode("0.1.94.31.6.255");

  private static final AttributeAddress ATTRIBUTE_CAPTURE_OBJECTS =
      new AttributeAddress(
          CLASS_ID, LOGICAL_NAME, ProfileGenericAttribute.CAPTURE_OBJECTS.attributeId());
  private static final String ATTRIBUTE_NAME_CAPTURE_OBJECTS = "capture objects";

  private static final AttributeAddress ATTRIBUTE_CAPTURE_PERIOD =
      new AttributeAddress(
          CLASS_ID, LOGICAL_NAME, ProfileGenericAttribute.CAPTURE_PERIOD.attributeId());
  private static final String ATTRIBUTE_NAME_CAPTURE_PERIOD = "capture period";

  private static final ObisCode LOGICAL_NAME_CLOCK = new ObisCode("0.0.1.0.0.255");
  private static final int CLASS_ID_CLOCK = InterfaceClass.CLOCK.id();
  private static final byte ATTRIBUTE_INDEX_CLOCK_TIME = (byte) ClockAttribute.TIME.attributeId();
  private static final DataObject CLOCK_TIME_DEFINITION =
      DataObject.newStructureData(
          DataObject.newUInteger16Data(CLASS_ID_CLOCK),
              DataObject.newOctetStringData(LOGICAL_NAME_CLOCK.bytes()),
          DataObject.newInteger8Data(ATTRIBUTE_INDEX_CLOCK_TIME), DataObject.newUInteger16Data(0));

  @Autowired private MapperFacade configurationMapper;

  public ConfigureDefinableLoadProfileCommandExecutor() {
    super(DefinableLoadProfileConfigurationDto.class);
  }

  @Override
  public ActionResponseDto asBundleResponse(final Void executionResult)
      throws ProtocolAdapterException {
    /*
     * Always successful, otherwise a ProtocolAdapterException was thrown
     * before.
     */
    return new ActionResponseDto("Configure definable load profile was successful");
  }

  @Override
  public Void execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final DefinableLoadProfileConfigurationDto definableLoadProfileConfiguration)
      throws ProtocolAdapterException {

    if (definableLoadProfileConfiguration.hasCaptureObjects()) {
      this.writeCaptureObjects(conn, definableLoadProfileConfiguration.getCaptureObjects());
    }

    if (definableLoadProfileConfiguration.hasCapturePeriod()) {
      this.writeCapturePeriod(conn, definableLoadProfileConfiguration.getCapturePeriod());
    }

    return null;
  }

  private void writeCaptureObjects(
      final DlmsConnectionManager conn, final List<CaptureObjectDefinitionDto> captureObjects)
      throws ProtocolAdapterException {

    this.dlmsLogWrite(conn, ATTRIBUTE_CAPTURE_OBJECTS, ATTRIBUTE_NAME_CAPTURE_OBJECTS);
    this.writeAttribute(
        conn,
        new SetParameter(
            ATTRIBUTE_CAPTURE_OBJECTS,
            DataObject.newArrayData(this.mapCaptureObjects(captureObjects))),
        ATTRIBUTE_NAME_CAPTURE_OBJECTS);
  }

  private List<DataObject> mapCaptureObjects(
      final List<CaptureObjectDefinitionDto> captureObjects) {
    final List<DataObject> captureObjectsArray = new ArrayList<>();
    /*
     * Always make sure the capture object definition of the clock time is
     * included as first capture object in the list, and that the clock time
     * is not included anywhere else as part of the capture objects.
     */
    captureObjectsArray.add(CLOCK_TIME_DEFINITION);
    for (final CaptureObjectDefinitionDto captureObject : captureObjects) {
      if (!this.isClockTimeDefinition(captureObject)) {
        captureObjectsArray.add(this.configurationMapper.map(captureObject, DataObject.class));
      }
    }
    return captureObjectsArray;
  }

  private boolean isClockTimeDefinition(final CaptureObjectDefinitionDto captureObject) {
    return CLASS_ID_CLOCK == captureObject.getClassId()
        && Arrays.equals(LOGICAL_NAME_CLOCK.bytes(), captureObject.getLogicalName().toByteArray())
        && ATTRIBUTE_INDEX_CLOCK_TIME == captureObject.getAttributeIndex();
  }

  private void writeCapturePeriod(final DlmsConnectionManager conn, final long capturePeriod)
      throws ProtocolAdapterException {

    this.dlmsLogWrite(conn, ATTRIBUTE_CAPTURE_PERIOD, ATTRIBUTE_NAME_CAPTURE_PERIOD);
    this.writeAttribute(
        conn,
        new SetParameter(ATTRIBUTE_CAPTURE_PERIOD, DataObject.newUInteger32Data(capturePeriod)),
        ATTRIBUTE_NAME_CAPTURE_PERIOD);
  }

  private void writeAttribute(
      final DlmsConnectionManager conn, final SetParameter parameter, final String attributeName)
      throws ProtocolAdapterException {
    try {
      final AccessResultCode result = conn.getConnection().set(parameter);
      if (!result.equals(AccessResultCode.SUCCESS)) {
        throw new ProtocolAdapterException(
            String.format(
                "Attribute '%s' of the definable load profile was not set successfully. ResultCode: %s",
                attributeName, result.name()));
      }
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void dlmsLogWrite(
      final DlmsConnectionManager conn,
      final AttributeAddress attribute,
      final String attributeName) {
    conn.getDlmsMessageListener()
        .setDescription(
            "Writing definable load profile attribute '"
                + attributeName
                + "': "
                + JdlmsObjectToStringUtil.describeAttributes(attribute));
  }
}
