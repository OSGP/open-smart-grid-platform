// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.MapperFacade;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
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
  private final DlmsHelper dlmsHelper;

  public SetClockConfigurationCommandExecutor(
      final MapperFacade configurationMapper,
      final ObjectConfigService objectConfigService,
      final DlmsHelper dlmsHelper) {
    super(SetClockConfigurationRequestDto.class);
    this.configurationMapper = configurationMapper;
    this.objectConfigService = objectConfigService;
    this.dlmsHelper = dlmsHelper;
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

    final List<SetParameter> parameters = new ArrayList<>();
    final AttributeAddress attributeTimeZone =
        this.getClockAttributeAddress(device, ClockAttribute.TIME_ZONE);
    parameters.add(
        new SetParameter(
            attributeTimeZone, DataObject.newInteger16Data(object.getTimeZoneOffset())));

    final AttributeAddress attributeDstBegin =
        this.getClockAttributeAddress(device, ClockAttribute.DAYLIGHT_SAVINGS_BEGIN);
    final CosemDateTime daylightSavingsBegin =
        this.configurationMapper.map(object.getDaylightSavingsBegin(), CosemDateTime.class);
    parameters.add(
        new SetParameter(
            attributeDstBegin, DataObject.newOctetStringData(daylightSavingsBegin.encode())));

    final AttributeAddress attributeDstEnd =
        this.getClockAttributeAddress(device, ClockAttribute.DAYLIGHT_SAVINGS_END);
    final CosemDateTime daylightSavingsEnd =
        this.configurationMapper.map(object.getDaylightSavingsEnd(), CosemDateTime.class);
    parameters.add(
        new SetParameter(
            attributeDstEnd, DataObject.newOctetStringData(daylightSavingsEnd.encode())));

    final AttributeAddress attributeDstEnabled =
        this.getClockAttributeAddress(device, ClockAttribute.DAYLIGHT_SAVINGS_ENABLED);
    parameters.add(
        new SetParameter(
            attributeDstEnabled, DataObject.newBoolData(object.isDaylightSavingsEnabled())));

    final List<AccessResultCode> results = this.dlmsHelper.setWithList(conn, device, parameters);
    if (!results.stream().allMatch(result -> result.equals(AccessResultCode.SUCCESS))) {
      throw new ProtocolAdapterException(
          String.format(
              "Clock configuration was not set successfully for device %s. ResultCode: %s",
              device.getDeviceIdentification(),
              results.stream()
                  .filter(result -> !result.equals(AccessResultCode.SUCCESS))
                  .toList()));
    }

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
}
