/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.io.IOException;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SynchronizeTimeCommandExecutor
    extends AbstractCommandExecutor<SynchronizeTimeRequestDto, AccessResultCode> {

  private static final ObisCode LOGICAL_NAME = new ObisCode("0.0.1.0.0.255");

  private static final AttributeAddress ATTRIBUTE_TIME =
      new AttributeAddress(
          InterfaceClass.CLOCK.id(), LOGICAL_NAME, ClockAttribute.TIME.attributeId());

  @Autowired private DlmsHelper dlmsHelper;

  public SynchronizeTimeCommandExecutor() {
    super(SynchronizeTimeRequestDto.class);
  }

  @Override
  public SynchronizeTimeRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return (SynchronizeTimeRequestDto) bundleInput;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Synchronizing time was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SynchronizeTimeRequestDto synchronizeTimeRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    final DateTime dt = DateTime.now();
    final DataObject time =
        this.dlmsHelper.asDataObject(
            dt, synchronizeTimeRequestDto.getDeviation(), synchronizeTimeRequestDto.isDst());

    final SetParameter setParameter = new SetParameter(ATTRIBUTE_TIME, time);

    conn.getDlmsMessageListener()
        .setDescription(
            "SynchronizeTime to "
                + dt
                + ", set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(ATTRIBUTE_TIME));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
