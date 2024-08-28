// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP;

import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.EncryptionKeyStatusType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class GetMbusEncryptionKeyStatusCommandExecutor
    extends AbstractCommandExecutor<
        GetMbusEncryptionKeyStatusRequestDto, GetMbusEncryptionKeyStatusResponseDto> {

  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public GetMbusEncryptionKeyStatusCommandExecutor(
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(GetMbusEncryptionKeyStatusRequestDto.class);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public GetMbusEncryptionKeyStatusResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetMbusEncryptionKeyStatusRequestDto request,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final EncryptionKeyStatusTypeDto encryptionKeyStatusType =
        this.getEncryptionKeyStatusTypeDto(request.getChannel(), conn, device);
    return new GetMbusEncryptionKeyStatusResponseDto(
        request.getMbusDeviceIdentification(), encryptionKeyStatusType);
  }

  public EncryptionKeyStatusTypeDto getEncryptionKeyStatusTypeDto(
      final short channel, final DlmsConnectionManager conn, final DlmsDevice device)
      throws ProtocolAdapterException {

    final CosemObjectAccessor cosemObjectAccessor =
        this.createCosemObjectAccessor(conn, device, channel);
    final AttributeAddress attributeAddress =
        cosemObjectAccessor.createAttributeAddress(MbusClientAttribute.ENCRYPTION_KEY_STATUS);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetMbusEncryptionKeyStatusByChannel, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    log.debug(
        "Retrieving current M-Bus encryption key status by issuing get request for class id: {}, obis code: "
            + "{}, attribute id: {}",
        attributeAddress.getClassId(),
        attributeAddress.getInstanceId(),
        attributeAddress.getId());

    final DataObject dataObject = this.getValidatedResultData(conn, attributeAddress);

    return EncryptionKeyStatusTypeDto.valueOf(
        EncryptionKeyStatusType.fromValue(dataObject.getValue()).name());
  }

  private CosemObjectAccessor createCosemObjectAccessor(
      final DlmsConnectionManager conn, final DlmsDevice device, final short channel)
      throws NotSupportedByProtocolException {
    return new CosemObjectAccessor(
        conn,
        this.objectConfigServiceHelper,
        MBUS_CLIENT_SETUP,
        Protocol.forDevice(device),
        channel);
  }
}
