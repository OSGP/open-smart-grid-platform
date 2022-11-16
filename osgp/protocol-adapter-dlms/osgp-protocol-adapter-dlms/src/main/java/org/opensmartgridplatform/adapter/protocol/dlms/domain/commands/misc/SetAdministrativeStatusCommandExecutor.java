/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.io.IOException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetAdministrativeStatusCommandExecutor
    extends AbstractCommandExecutor<AdministrativeStatusTypeDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetAdministrativeStatusCommandExecutor.class);

  private static final int CLASS_ID = 1;
  private static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.0.255");
  private static final int ATTRIBUTE_ID = 2;

  @Autowired private ConfigurationMapper configurationMapper;

  public SetAdministrativeStatusCommandExecutor() {
    super(AdministrativeStatusTypeDataDto.class);
  }

  @Override
  public AdministrativeStatusTypeDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final AdministrativeStatusTypeDataDto administrativeStatusTypeDataDto =
        (AdministrativeStatusTypeDataDto) bundleInput;

    return administrativeStatusTypeDataDto.getAdministrativeStatusType();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Set administrative status was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AdministrativeStatusTypeDto administrativeStatusType,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.debug(
        "Set administrative status by issuing get request for class id: {}, obis code: {}, attribute id: {}",
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID);

    final AttributeAddress attributeAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
    final DataObject value =
        DataObject.newEnumerateData(
            this.configurationMapper.map(administrativeStatusType, Integer.class));

    final SetParameter setParameter = new SetParameter(attributeAddress, value);

    conn.getDlmsMessageListener()
        .setDescription(
            "SetAdminstrativeStatus to "
                + administrativeStatusType
                + ", set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
