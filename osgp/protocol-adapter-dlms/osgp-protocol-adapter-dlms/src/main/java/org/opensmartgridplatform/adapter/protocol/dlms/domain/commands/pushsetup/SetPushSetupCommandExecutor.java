// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.io.IOException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;

public abstract class SetPushSetupCommandExecutor<T, R> extends AbstractCommandExecutor<T, R> {

  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  protected SetPushSetupCommandExecutor(
      final Class<? extends ActionRequestDto> clazz,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(clazz);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  protected AccessResultCode doSetRequest(
      final String pushSetup, final DlmsConnectionManager conn, final SetParameter setParameter) {

    conn.getDlmsMessageListener()
        .setDescription(
            pushSetup
                + ", set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(setParameter.getAttributeAddress()));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  protected SendDestinationAndMethodDto getUpdatedSendDestinationAndMethod(
      final SendDestinationAndMethodDto sendDestinationAndMethodDto, final DlmsDevice device) {
    return new SendDestinationAndMethodDto(
        this.getTransportServiceType(),
        sendDestinationAndMethodDto.getDestination(),
        this.getMessageType(device));
  }

  protected abstract TransportServiceTypeDto getTransportServiceType();

  private MessageTypeDto getMessageType(final DlmsDevice device) {
    if (Protocol.forDevice(device).isSmr5()) {
      return MessageTypeDto.A_XDR_ENCODED_X_DLMS_APDU;
    } else {
      return MessageTypeDto.MANUFACTURER_SPECIFIC;
    }
  }

  protected AttributeAddress getSendDestinationAndMethodAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType)
      throws NotSupportedByProtocolException {
    return this.getAttributeAddress(
        protocol, dlmsObjectType, PushSetupAttribute.SEND_DESTINATION_AND_METHOD.attributeId());
  }

  protected AttributeAddress getPushObjectListAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType)
      throws NotSupportedByProtocolException {
    return this.getAttributeAddress(
        protocol, dlmsObjectType, PushSetupAttribute.PUSH_OBJECT_LIST.attributeId());
  }

  protected AttributeAddress getCommunicationWindowAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType)
      throws NotSupportedByProtocolException {
    return this.getAttributeAddress(
        protocol, dlmsObjectType, PushSetupAttribute.COMMUNICATION_WINDOW.attributeId());
  }

  protected AttributeAddress getAttributeAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType, final int attributeId)
      throws NotSupportedByProtocolException {
    return this.objectConfigServiceHelper
        .findOptionalAttributeAddress(protocol, dlmsObjectType, null, attributeId)
        .orElseThrow(
            () ->
                new NotSupportedByProtocolException(
                    String.format(
                        "No address found for %s in protocol %s %s",
                        dlmsObjectType.name(), protocol.getName(), protocol.getVersion())));
  }
}
