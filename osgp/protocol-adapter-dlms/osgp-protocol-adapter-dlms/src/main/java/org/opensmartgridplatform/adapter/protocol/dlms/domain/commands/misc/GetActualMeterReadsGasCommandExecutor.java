// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.CAPTURE_TIME;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.SCALER_UNIT;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.VALUE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_MASTER_VALUE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ExtendedRegister;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component()
public class GetActualMeterReadsGasCommandExecutor
    extends AbstractCommandExecutor<ActualMeterReadsQueryDto, MeterReadsGasResponseDto> {

  private static final int INDEX_VALUE = 0;
  private static final int INDEX_TIME = 1;
  private static final int INDEX_SCALER_UNIT = 2;

  private final DlmsHelper dlmsHelper;

  private final ObjectConfigService objectConfigService;

  public GetActualMeterReadsGasCommandExecutor(
      final ObjectConfigService objectConfigService, final DlmsHelper dlmsHelper) {
    super(ActualMeterReadsDataGasDto.class);

    this.objectConfigService = objectConfigService;
    this.dlmsHelper = dlmsHelper;
  }

  @Override
  public ActualMeterReadsQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final ActualMeterReadsDataGasDto actualMeterReadsDataGasDto =
        (ActualMeterReadsDataGasDto) bundleInput;

    return new ActualMeterReadsQueryDto(actualMeterReadsDataGasDto.getChannel());
  }

  @Override
  public MeterReadsGasResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualMeterReadsQueryDto actualMeterReadsRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    if (actualMeterReadsRequest == null || !actualMeterReadsRequest.isMbusQuery()) {
      throw new IllegalArgumentException(
          "ActualMeterReadsQuery for energy reads should not be null and be about gas.");
    }

    final ChannelDto channel = actualMeterReadsRequest.getChannel();

    final ExtendedRegister extendedRegister =
        this.getCosemObjectFromConfig(device, actualMeterReadsRequest);

    final AttributeAddress[] atttributeAddresses = this.getAddresses(extendedRegister);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReadsGas for channel "
                + channel
                + ", retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(atttributeAddresses));

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual meter reads for mbus " + channel, atttributeAddresses);

    final GetResult value = getResultList.get(INDEX_VALUE);
    final String scalerUnit = this.getScalerUnit(extendedRegister, getResultList);
    final DlmsMeterValueDto consumption =
        this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            value, scalerUnit, "retrieve scaled value for mbus " + channel);

    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(getResultList.get(INDEX_TIME), "captureTime gas");
    final Date captureTime;
    if (cosemDateTime.isDateTimeSpecified()) {
      captureTime = cosemDateTime.asDateTime().toDate();
    } else {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for M-Bus Capture Time");
    }

    return new MeterReadsGasResponseDto(new Date(), consumption, captureTime);
  }

  private String getScalerUnit(
      final ExtendedRegister extendedRegister, final List<GetResult> getResultList)
      throws ProtocolAdapterException {
    if (extendedRegister.needsScalerUnitFromMeter()) {
      final GetResult scalerUnitResult = getResultList.get(INDEX_SCALER_UNIT);
      return this.dlmsHelper.getScalerUnit(scalerUnitResult.getResultData(), "scaler unit");
    } else {
      return extendedRegister.getScalerUnit();
    }
  }

  private ExtendedRegister getCosemObjectFromConfig(
      final DlmsDevice device, final ActualMeterReadsQueryDto query)
      throws ProtocolAdapterException {
    final CosemObject cosemObject;
    try {
      cosemObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(), device.getProtocolVersion(), MBUS_MASTER_VALUE);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }

    if (!(cosemObject instanceof ExtendedRegister)) {
      throw new ProtocolAdapterException("Expected an ExtendedRegister");
    }

    return (ExtendedRegister)
        this.updateCosemObjectWithChannel(cosemObject, query.getChannel().getChannelNumber());
  }

  private CosemObject updateCosemObjectWithChannel(final CosemObject cosemObject, final int channel)
      throws ProtocolAdapterException {
    try {
      if (cosemObject.hasWildcardChannel()) {
        return cosemObject.copyWithNewObis(
            cosemObject.getObis().replace("x", String.valueOf(channel)));
      } else {
        throw new ProtocolAdapterException(
            "Expected an M-Bus master value with a wildcard channel");
      }
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }
  }

  private AttributeAddress[] getAddresses(final CosemObject object) {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();

    attributeAddresses.add(
        new AttributeAddress(object.getClassId(), object.getObis(), VALUE.attributeId()));
    attributeAddresses.add(
        new AttributeAddress(object.getClassId(), object.getObis(), CAPTURE_TIME.attributeId()));
    if (object instanceof final ExtendedRegister register && register.needsScalerUnitFromMeter()) {
      attributeAddresses.add(
          new AttributeAddress(object.getClassId(), object.getObis(), SCALER_UNIT.attributeId()));
    }

    return attributeAddresses.toArray(new AttributeAddress[0]);
  }
}
