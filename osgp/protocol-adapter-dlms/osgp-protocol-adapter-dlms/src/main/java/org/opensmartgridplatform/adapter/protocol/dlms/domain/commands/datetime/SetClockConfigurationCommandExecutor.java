// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.io.IOException;
import ma.glasnost.orika.MapperFacade;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class SetClockConfigurationCommandExecutor
    extends AbstractCommandExecutor<SetClockConfigurationRequestDto, Void> {

  private final MapperFacade configurationMapper;
  private final ObjectConfigService objectConfigService;

  public SetClockConfigurationCommandExecutor(
      final MapperFacade configurationMapper, final ObjectConfigService objectConfigService) {
    super(SetClockConfigurationRequestDto.class);
    this.configurationMapper = configurationMapper;
    this.objectConfigService = objectConfigService;
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

    final AttributeAddress attributeTimeZone =
        this.getClockAttributeAddress(device, ClockAttribute.TIME_ZONE);
    this.dlmsLogWrite(conn, attributeTimeZone);
    this.writeAttribute(
        conn,
        new SetParameter(
            attributeTimeZone, DataObject.newInteger16Data(object.getTimeZoneOffset())),
        "Timezone");

    final AttributeAddress attributeDstBegin =
        this.getClockAttributeAddress(device, ClockAttribute.DAYLIGHT_SAVINGS_BEGIN);
    final CosemDateTime daylightSavingsBegin =
        this.configurationMapper.map(object.getDaylightSavingsBegin(), CosemDateTime.class);
    this.dlmsLogWrite(conn, attributeDstBegin);
    this.writeAttribute(
        conn,
        new SetParameter(
            attributeDstBegin, DataObject.newOctetStringData(daylightSavingsBegin.encode())),
        "Daylight savings begin");

    final AttributeAddress attributeDstEnd =
        this.getClockAttributeAddress(device, ClockAttribute.DAYLIGHT_SAVINGS_END);
    final CosemDateTime daylightSavingsEnd =
        this.configurationMapper.map(object.getDaylightSavingsEnd(), CosemDateTime.class);
    this.dlmsLogWrite(conn, attributeDstEnd);
    this.writeAttribute(
        conn,
        new SetParameter(
            attributeDstEnd, DataObject.newOctetStringData(daylightSavingsEnd.encode())),
        "Daylight savings end");

    final AttributeAddress attributeDstEnabled =
        this.getClockAttributeAddress(device, ClockAttribute.DAYLIGHT_SAVINGS_ENABLED);
    this.dlmsLogWrite(conn, attributeDstEnabled);
    this.writeAttribute(
        conn,
        new SetParameter(
            attributeDstEnabled, DataObject.newBoolData(object.isDaylightSavingsEnabled())),
        "Daylight savings enabled");
    return null;
  }

  private AttributeAddress getClockAttributeAddress(
      final DlmsDevice device, final ClockAttribute clockAttribute)
      throws ProtocolAdapterException {
    try {
      final CosemObject cosemObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(), device.getProtocolVersion(), DlmsObjectType.CLOCK);
      return new AttributeAddress(
          cosemObject.getClassId(), cosemObject.getObis(), clockAttribute.attributeId());
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }
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
