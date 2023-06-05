// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.io.IOException;
import ma.glasnost.orika.MapperFacade;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetClockConfigurationCommandExecutor
    extends AbstractCommandExecutor<SetClockConfigurationRequestDto, Void> {

  private static final ObisCode LOGICAL_NAME = new ObisCode("0.0.1.0.0.255");

  private static final AttributeAddress ATTRIBUTE_TIME_ZONE =
      new AttributeAddress(
          InterfaceClass.CLOCK.id(), LOGICAL_NAME, ClockAttribute.TIME_ZONE.attributeId());

  private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_BEGIN =
      new AttributeAddress(
          InterfaceClass.CLOCK.id(),
          LOGICAL_NAME,
          ClockAttribute.DAYLIGHT_SAVINGS_BEGIN.attributeId());

  private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_END =
      new AttributeAddress(
          InterfaceClass.CLOCK.id(),
          LOGICAL_NAME,
          ClockAttribute.DAYLIGHT_SAVINGS_END.attributeId());

  private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_ENABLED =
      new AttributeAddress(
          InterfaceClass.CLOCK.id(),
          LOGICAL_NAME,
          ClockAttribute.DAYLIGHT_SAVINGS_ENABLED.attributeId());

  @Autowired private MapperFacade configurationMapper;

  public SetClockConfigurationCommandExecutor() {
    super(SetClockConfigurationRequestDto.class);
  }

  @Override
  public ActionResponseDto asBundleResponse(final Void executionResult)
      throws ProtocolAdapterException {
    /*
     * Always successful, otherwise a ProtocolAdapterException was thrown
     * before.
     */
    return new ActionResponseDto("Set clock configuration was successful");
  }

  @Override
  public Void execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetClockConfigurationRequestDto object,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    this.dlmsLogWrite(conn, ATTRIBUTE_TIME_ZONE);
    this.writeAttribute(
        conn,
        new SetParameter(
            ATTRIBUTE_TIME_ZONE, DataObject.newInteger16Data(object.getTimeZoneOffset())),
        "Timezone");

    final CosemDateTime daylightSavingsBegin =
        this.configurationMapper.map(object.getDaylightSavingsBegin(), CosemDateTime.class);
    this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_BEGIN);
    this.writeAttribute(
        conn,
        new SetParameter(
            ATTRIBUTE_DAYLIGHT_SAVINGS_BEGIN,
            DataObject.newOctetStringData(daylightSavingsBegin.encode())),
        "Daylight savings begin");

    final CosemDateTime daylightSavingsEnd =
        this.configurationMapper.map(object.getDaylightSavingsEnd(), CosemDateTime.class);
    this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_END);
    this.writeAttribute(
        conn,
        new SetParameter(
            ATTRIBUTE_DAYLIGHT_SAVINGS_END,
            DataObject.newOctetStringData(daylightSavingsEnd.encode())),
        "Daylight savinds end");

    this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_ENABLED);
    this.writeAttribute(
        conn,
        new SetParameter(
            ATTRIBUTE_DAYLIGHT_SAVINGS_ENABLED,
            DataObject.newBoolData(object.isDaylightSavingsEnabled())),
        "Daylight savings enabled");
    return null;
  }

  private void writeAttribute(
      final DlmsConnectionManager conn, final SetParameter parameter, final String attributeName)
      throws ProtocolAdapterException {
    try {
      final AccessResultCode result = conn.getConnection().set(parameter);
      if (!result.equals(AccessResultCode.SUCCESS)) {
        throw new ProtocolAdapterException(
            String.format(
                "Attribute '%s' of the clock configuration was not set successfully. ResultCode: %s",
                attributeName, result.name()));
      }
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void dlmsLogWrite(final DlmsConnectionManager conn, final AttributeAddress attribute) {
    conn.getDlmsMessageListener()
        .setDescription(
            "SetClockConfiguration, preparing to write attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attribute));
  }
}
