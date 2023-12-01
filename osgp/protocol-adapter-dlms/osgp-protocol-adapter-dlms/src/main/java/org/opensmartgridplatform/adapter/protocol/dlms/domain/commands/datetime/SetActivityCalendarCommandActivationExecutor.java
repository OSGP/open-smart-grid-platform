// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.io.IOException;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.method.ActivityCalendarMethod;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SetActivityCalendarCommandActivationExecutor
    extends AbstractCommandExecutor<Void, MethodResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetActivityCalendarCommandActivationExecutor.class);

  private final ObjectConfigService objectConfigService;

  SetActivityCalendarCommandActivationExecutor(final ObjectConfigService objectConfigService) {
    this.objectConfigService = objectConfigService;
  }

  @Override
  public MethodResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void v,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.debug("ACTIVATING PASSIVE CALENDAR");
    final CosemObject cosemObject;
    try {
      cosemObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(),
              device.getProtocolVersion(),
              DlmsObjectType.ACTIVITY_CALENDAR);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }

    final MethodParameter method =
        new MethodParameter(
            cosemObject.getClassId(),
            cosemObject.getObis(),
            ActivityCalendarMethod.ACTIVATE_PASSIVE_CALENDAR.getMethodId(),
            DataObject.newInteger8Data((byte) 0));

    conn.getDlmsMessageListener()
        .setDescription(
            "SetActivityCalendarActivation, call method: "
                + JdlmsObjectToStringUtil.describeMethod(method));

    final MethodResult methodResultCode;
    try {
      methodResultCode = conn.getConnection().action(method);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
    if (!MethodResultCode.SUCCESS.equals(methodResultCode.getResultCode())) {
      throw new ProtocolAdapterException(
          "Activating the activity calendar failed. MethodResult is: "
              + methodResultCode.getResultCode()
              + " ClassId: "
              + cosemObject.getClassId()
              + " obisCode: "
              + cosemObject.getObis()
              + " method id: "
              + ActivityCalendarMethod.ACTIVATE_PASSIVE_CALENDAR.getMethodId());
    }
    return MethodResultCode.SUCCESS;
  }
}
