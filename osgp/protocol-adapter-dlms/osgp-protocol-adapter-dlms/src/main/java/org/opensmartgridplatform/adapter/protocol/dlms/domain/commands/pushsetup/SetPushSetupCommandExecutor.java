// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.io.IOException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;

public abstract class SetPushSetupCommandExecutor<T, R> extends AbstractCommandExecutor<T, R> {

  protected static final int CLASS_ID = 40;
  protected static final int ATTRIBUTE_ID_PUSH_OBJECT_LIST = 2;
  protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;

  protected SetPushSetupCommandExecutor() {
    /*
     * No argument constructor for subclasses that do not act in a bundle
     * context, so they do not need to be looked up by ActionRequestDto
     * class.
     */
  }

  protected SetPushSetupCommandExecutor(final Class<? extends ActionRequestDto> clazz) {
    super(clazz);
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
}
