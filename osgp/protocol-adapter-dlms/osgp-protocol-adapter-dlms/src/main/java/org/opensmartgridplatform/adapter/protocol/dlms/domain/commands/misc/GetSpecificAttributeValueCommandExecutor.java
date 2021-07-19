/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetSpecificAttributeValueCommandExecutor
    extends AbstractCommandExecutor<SpecificAttributeValueRequestDto, String> {

  @Autowired private DlmsHelper dlmsHelper;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetSpecificAttributeValueCommandExecutor.class);

  public GetSpecificAttributeValueCommandExecutor() {
    super(SpecificAttributeValueRequestDto.class);
  }

  @Override
  public ActionResponseDto asBundleResponse(final String executionResult)
      throws ProtocolAdapterException {
    return new ActionResponseDto(executionResult);
  }

  @Override
  public String execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SpecificAttributeValueRequestDto requestData,
      final MessageMetadata messageMetadata)
      throws FunctionalException {

    final ObisCodeValuesDto obisCodeValues = requestData.getObisCode();
    final byte[] obisCodeBytes = {
      obisCodeValues.getA(),
      obisCodeValues.getB(),
      obisCodeValues.getC(),
      obisCodeValues.getD(),
      obisCodeValues.getE(),
      obisCodeValues.getF()
    };
    final ObisCode obisCode = new ObisCode(obisCodeBytes);

    LOGGER.debug(
        "Get specific attribute value, class id: {}, obis code: {}, attribute id: {}",
        requestData.getClassId(),
        obisCode,
        requestData.getAttribute());

    final AttributeAddress attributeAddress =
        new AttributeAddress(requestData.getClassId(), obisCode, requestData.getAttribute());

    conn.getDlmsMessageListener()
        .setDescription(
            "GetSpecificAttributeValue, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    final DataObject attributeValue = this.dlmsHelper.getAttributeValue(conn, attributeAddress);
    return this.dlmsHelper.getDebugInfo(attributeValue);
  }
}
