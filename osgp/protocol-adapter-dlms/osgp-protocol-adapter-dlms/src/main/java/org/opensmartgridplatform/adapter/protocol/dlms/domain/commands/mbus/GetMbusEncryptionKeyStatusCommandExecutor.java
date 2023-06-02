//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.util.HashMap;
import java.util.Map;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.EncryptionKeyStatusType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class GetMbusEncryptionKeyStatusCommandExecutor
    extends AbstractCommandExecutor<
        GetMbusEncryptionKeyStatusRequestDto, GetMbusEncryptionKeyStatusResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetMbusEncryptionKeyStatusCommandExecutor.class);

  private static final int CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
  private static final Map<Short, ObisCode> OBIS_CODES = new HashMap<>();
  private static final int ATTRIBUTE_ID = MbusClientAttribute.ENCRYPTION_KEY_STATUS.attributeId();

  static {
    OBIS_CODES.put((short) 1, new ObisCode("0.1.24.1.0.255"));
    OBIS_CODES.put((short) 2, new ObisCode("0.2.24.1.0.255"));
    OBIS_CODES.put((short) 3, new ObisCode("0.3.24.1.0.255"));
    OBIS_CODES.put((short) 4, new ObisCode("0.4.24.1.0.255"));
  }

  public GetMbusEncryptionKeyStatusCommandExecutor() {
    super(GetMbusEncryptionKeyStatusRequestDto.class);
  }

  @Override
  public GetMbusEncryptionKeyStatusResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetMbusEncryptionKeyStatusRequestDto request,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final EncryptionKeyStatusTypeDto encryptionKeyStatusType =
        this.getEncryptionKeyStatusTypeDto(request.getChannel(), conn);
    return new GetMbusEncryptionKeyStatusResponseDto(
        request.getMbusDeviceIdentification(), encryptionKeyStatusType);
  }

  public EncryptionKeyStatusTypeDto getEncryptionKeyStatusTypeDto(
      final short channel, final DlmsConnectionManager conn) throws ProtocolAdapterException {

    final ObisCode obisCode = OBIS_CODES.get(channel);

    final AttributeAddress getParameter = new AttributeAddress(CLASS_ID, obisCode, ATTRIBUTE_ID);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetMbusEncryptionKeyStatusByChannel, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(getParameter));

    LOGGER.debug(
        "Retrieving current M-Bus encryption key status by issuing get request for class id: {}, obis code: "
            + "{}, attribute id: {}",
        CLASS_ID,
        obisCode,
        ATTRIBUTE_ID);

    final DataObject dataObject = this.getValidatedResultData(conn, getParameter);

    return EncryptionKeyStatusTypeDto.valueOf(
        EncryptionKeyStatusType.fromValue(dataObject.getValue()).name());
  }
}
