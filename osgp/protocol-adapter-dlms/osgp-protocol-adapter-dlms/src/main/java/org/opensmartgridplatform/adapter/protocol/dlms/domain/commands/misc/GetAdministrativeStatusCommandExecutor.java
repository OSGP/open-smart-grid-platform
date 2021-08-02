/*
 * Copyright 2015 Smart Society Services B.V.
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
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetAdministrativeStatusCommandExecutor
    extends AbstractCommandExecutor<Void, AdministrativeStatusTypeDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetAdministrativeStatusCommandExecutor.class);

  private static final int CLASS_ID = 1;
  private static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.0.255");
  private static final int ATTRIBUTE_ID = 2;

  @Autowired private ConfigurationMapper configurationMapper;

  public GetAdministrativeStatusCommandExecutor() {
    super(GetAdministrativeStatusDataDto.class);
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return null;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AdministrativeStatusTypeDto executionResult)
      throws ProtocolAdapterException {

    return new AdministrativeStatusTypeResponseDto(executionResult);
  }

  @Override
  public AdministrativeStatusTypeDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void useless,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AttributeAddress getParameter = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetAdministrativeStatus, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(getParameter));

    LOGGER.info(
        "Retrieving current administrative status by issuing get request for class id: {}, obis code: {}, "
            + "attribute id: {}",
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID);

    final DataObject dataObject = this.getValidatedResultData(conn, getParameter);

    return this.configurationMapper.map(dataObject.getValue(), AdministrativeStatusTypeDto.class);
  }
}
