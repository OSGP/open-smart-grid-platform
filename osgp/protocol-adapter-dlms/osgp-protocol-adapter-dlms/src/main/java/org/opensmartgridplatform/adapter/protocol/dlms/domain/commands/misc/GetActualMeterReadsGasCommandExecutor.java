// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.CAPTURE_TIME;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.SCALER_UNIT;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.VALUE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_MASTER_VALUE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_MASTER_VALUE_5MIN;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.CombinedDeviceModelCode;
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

    // Get devicemodel from messageMetaData. The devicemodel determines which scaler_unit to use.
    final CombinedDeviceModelCode combinedDeviceModelCode =
        CombinedDeviceModelCode.parse(messageMetadata.getDeviceModelCode());

    final ChannelDto channel = actualMeterReadsRequest.getChannel();

    // Get object from config, based on device, channel and devicemodel.
    final ExtendedRegister extendedRegister =
        this.getCosemObjectFromConfig(
            device,
            channel,
            combinedDeviceModelCode.getCodeFromChannel(channel.getChannelNumber()));

    // Create the addresses to read: value, captureTime and (if needed) the scaler_unit.
    // Note: the order is important. The meter will return the values in the same order.
    final AttributeAddress[] atttributeAddresses = this.getAddresses(extendedRegister);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReadsGas for channel "
                + channel
                + ", retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(atttributeAddresses));

    // Read the data from the meter
    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual meter reads for mbus " + channel, atttributeAddresses);

    // Get the data from the results retrieved from the meter.
    // The results are in the same order as requested.
    final GetResult value = getResultList.get(INDEX_VALUE);
    final String scalerUnit = this.getScalerUnit(extendedRegister, getResultList);
    final DlmsMeterValueDto consumption =
        this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            value, scalerUnit, "retrieve scaled value for mbus " + channel);
    final Date captureTime = this.getCaptureTime(getResultList);

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

  private Date getCaptureTime(final List<GetResult> getResultList) throws ProtocolAdapterException {
    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(getResultList.get(INDEX_TIME), "captureTime gas");
    final Date captureTime;
    if (cosemDateTime.isDateTimeSpecified()) {
      captureTime = cosemDateTime.asDateTime().toDate();
    } else {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for M-Bus Capture Time");
    }
    return captureTime;
  }

  private ExtendedRegister getCosemObjectFromConfig(
      final DlmsDevice device, final ChannelDto channel, final String deviceModel)
      throws ProtocolAdapterException {
    final CosemObject cosemObject;
    try {
      // Some profiles have multiple M-Bus master values. If possible use the M-Bus master value
      // with 5 min values. Otherwise, use the M-Bus master value with hourly values.
      final Optional<CosemObject> optionalMbusMasterValue5min =
          this.objectConfigService.getOptionalCosemObject(
              device.getProtocolName(),
              device.getProtocolVersion(),
              MBUS_MASTER_VALUE_5MIN,
              deviceModel);
      if (optionalMbusMasterValue5min.isPresent()) {
        cosemObject = optionalMbusMasterValue5min.get();
      } else {
        cosemObject =
            this.objectConfigService.getCosemObject(
                device.getProtocolName(),
                device.getProtocolVersion(),
                MBUS_MASTER_VALUE,
                deviceModel);
      }
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }

    if (!(cosemObject instanceof ExtendedRegister)) {
      throw new ProtocolAdapterException("Expected an ExtendedRegister");
    }

    return (ExtendedRegister)
        this.updateCosemObjectWithChannel(cosemObject, channel.getChannelNumber());
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

    // Only request the scaler_unit when the scaler_unit could not be determined by the config and
    // the device model.
    if (object instanceof final ExtendedRegister register && register.needsScalerUnitFromMeter()) {
      attributeAddresses.add(
          new AttributeAddress(object.getClassId(), object.getObis(), SCALER_UNIT.attributeId()));
    }

    return attributeAddresses.toArray(new AttributeAddress[0]);
  }
}
