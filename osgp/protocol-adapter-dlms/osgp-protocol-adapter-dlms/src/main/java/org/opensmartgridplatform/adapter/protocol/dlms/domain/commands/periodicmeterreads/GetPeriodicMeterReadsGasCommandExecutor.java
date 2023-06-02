//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPeriodicMeterReadsGasCommandExecutor
    extends AbstractPeriodicMeterReadsCommandExecutor<
        PeriodicMeterReadsRequestDto, PeriodicMeterReadGasResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

  private static final String GAS_VALUE = "gasValue";
  private static final String PERIODIC_G_METER_READS = "Periodic G-Meter Reads";
  private static final String UNEXPECTED_VALUE =
      "Unexpected null/unspecified value for Gas Capture Time";
  private static final String FORMAT_DESCRIPTION =
      "GetPeriodicMeterReadsGas for channel %s, %s from %s until %s, " + "retrieve attribute: %s";

  private final DlmsHelper dlmsHelper;
  private final DlmsObjectConfigService dlmsObjectConfigService;

  @Autowired
  public GetPeriodicMeterReadsGasCommandExecutor(
      final DlmsHelper dlmsHelper,
      final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
      final DlmsObjectConfigService dlmsObjectConfigService) {
    super(PeriodicMeterReadsGasRequestDto.class, amrProfileStatusCodeHelper);
    this.dlmsHelper = dlmsHelper;
    this.dlmsObjectConfigService = dlmsObjectConfigService;
  }

  @Override
  public PeriodicMeterReadsRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final PeriodicMeterReadsGasRequestDto periodicMeterReadsGasRequestDto =
        (PeriodicMeterReadsGasRequestDto) bundleInput;

    return new PeriodicMeterReadsRequestDto(
        periodicMeterReadsGasRequestDto.getPeriodType(),
        periodicMeterReadsGasRequestDto.getBeginDate(),
        periodicMeterReadsGasRequestDto.getEndDate(),
        periodicMeterReadsGasRequestDto.getChannel());
  }

  @Override
  public PeriodicMeterReadGasResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PeriodicMeterReadsRequestDto periodicMeterReadsQuery,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    if (periodicMeterReadsQuery == null) {
      throw new IllegalArgumentException(
          "PeriodicMeterReadsQuery should contain PeriodType, BeginDate and EndDate.");
    }

    final PeriodTypeDto queryPeriodType = periodicMeterReadsQuery.getPeriodType();
    final DateTime from =
        DlmsDateTimeConverter.toDateTime(
            periodicMeterReadsQuery.getBeginDate(), device.getTimezone());
    final DateTime to =
        DlmsDateTimeConverter.toDateTime(
            periodicMeterReadsQuery.getEndDate(), device.getTimezone());

    final AttributeAddressForProfile profileBufferAddress =
        this.getProfileBufferAddress(
            queryPeriodType, periodicMeterReadsQuery.getChannel(), from, to, device);

    final List<AttributeAddress> scalerUnitAddresses =
        this.getScalerUnitAddresses(periodicMeterReadsQuery.getChannel(), profileBufferAddress);

    final Optional<ProfileCaptureTime> intervalTime =
        this.getProfileCaptureTime(device, this.dlmsObjectConfigService, Medium.GAS);

    LOGGER.info(
        "Retrieving current billing period and profiles for gas for period type: {}, from: "
            + "{}, to: {}",
        queryPeriodType,
        from,
        to);

    /*
     * workaround for a problem when using with_list and retrieving a profile
     * buffer, this will be returned erroneously.
     */
    final List<GetResult> getResultList = new ArrayList<>();

    final List<AttributeAddress> allAttributeAddresses = new ArrayList<>();
    allAttributeAddresses.add(profileBufferAddress.getAttributeAddress());
    allAttributeAddresses.addAll(scalerUnitAddresses);

    for (final AttributeAddress address : allAttributeAddresses) {

      conn.getDlmsMessageListener()
          .setDescription(
              String.format(
                  FORMAT_DESCRIPTION,
                  periodicMeterReadsQuery.getChannel(),
                  queryPeriodType,
                  from,
                  to,
                  JdlmsObjectToStringUtil.describeAttributes(address)));

      getResultList.addAll(
          this.dlmsHelper.getAndCheck(
              conn,
              device,
              "retrieve periodic meter reads for "
                  + queryPeriodType
                  + ", channel "
                  + periodicMeterReadsQuery.getChannel(),
              address));
    }

    LOGGER.debug("Received getResult: {} ", getResultList);

    final DataObject resultData =
        this.dlmsHelper.readDataObject(getResultList.get(0), PERIODIC_G_METER_READS);
    final List<DataObject> bufferedObjectsList = resultData.getValue();

    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads = new ArrayList<>();
    for (final DataObject bufferedObject : bufferedObjectsList) {
      final List<DataObject> bufferedObjectValue = bufferedObject.getValue();

      try {
        periodicMeterReads.add(
            this.convertToResponseItem(
                new ConversionContext(
                    periodicMeterReadsQuery,
                    bufferedObjectValue,
                    getResultList,
                    profileBufferAddress,
                    scalerUnitAddresses,
                    intervalTime),
                periodicMeterReads));
      } catch (final BufferedDateTimeValidationException e) {
        LOGGER.warn(e.getMessage(), e);
      }
    }

    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReadsWithinRequestedPeriod =
        periodicMeterReads.stream()
            .filter(
                meterRead ->
                    this.validateDateTime(meterRead.getLogTime(), from.toDate(), to.toDate()))
            .toList();

    LOGGER.debug("Resulting periodicMeterReads: {} ", periodicMeterReads);

    return new PeriodicMeterReadGasResponseDto(
        queryPeriodType, periodicMeterReadsWithinRequestedPeriod);
  }

  private PeriodicMeterReadsGasResponseItemDto convertToResponseItem(
      final ConversionContext ctx,
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final Optional<Date> previousLogTime = this.getPreviousLogTime(periodicMeterReads);
    final Date logTime = this.readClock(ctx, previousLogTime, this.dlmsHelper);

    final AmrProfileStatusCodeDto status =
        this.readStatus(ctx.bufferedObjects, ctx.attributeAddressForProfile);
    final DataObject gasValue = this.readValue(ctx.bufferedObjects, ctx.attributeAddressForProfile);
    final DataObject scalerUnit =
        this.readScalerUnit(
            ctx.getResultList,
            ctx.attributeAddresses,
            ctx.attributeAddressForProfile,
            ctx.periodicMeterReadsQuery.getChannel().getChannelNumber());

    final Optional<Date> previousCaptureTime = this.getPreviousCaptureTime(periodicMeterReads);
    final Date captureTime = this.readCaptureTime(ctx, previousCaptureTime);

    LOGGER.debug("Converting bufferObject with value: {} ", ctx.bufferedObjects);
    LOGGER.debug(
        "Resulting values: LogTime: {}, status: {}, gasValue {}, scalerUnit: {}, captureTime {} ",
        logTime,
        status,
        gasValue,
        scalerUnit,
        captureTime);

    return new PeriodicMeterReadsGasResponseItemDto(
        logTime,
        this.dlmsHelper.getScaledMeterValue(gasValue, scalerUnit, GAS_VALUE),
        captureTime,
        status);
  }

  private Optional<Date> getPreviousLogTime(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads) {

    if (periodicMeterReads.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(periodicMeterReads.get(periodicMeterReads.size() - 1).getLogTime());
  }

  private Optional<Date> getPreviousCaptureTime(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads) {

    if (periodicMeterReads.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(periodicMeterReads.get(periodicMeterReads.size() - 1).getCaptureTime());
  }

  private DataObject readValue(
      final List<DataObject> bufferedObjects,
      final AttributeAddressForProfile attributeAddressForProfile) {

    final Integer valueIndex =
        attributeAddressForProfile.getIndex(DlmsObjectType.MBUS_MASTER_VALUE, 2);

    DataObject value = null;

    if (valueIndex != null) {
      value = bufferedObjects.get(valueIndex);
    }

    return value;
  }

  private DataObject readScalerUnit(
      final List<GetResult> getResultList,
      final List<AttributeAddress> attributeAddresses,
      final AttributeAddressForProfile attributeAddressForProfile,
      final Integer channel)
      throws ProtocolAdapterException {

    final DlmsCaptureObject captureObject =
        attributeAddressForProfile.getCaptureObject(DlmsObjectType.MBUS_MASTER_VALUE);

    int index = 0;
    Integer scalerUnitIndex = null;
    for (final AttributeAddress address : attributeAddresses) {
      final String obisCode =
          captureObject.getRelatedObject().getObisCodeAsString().replace("<c>", channel.toString());
      if (address.getInstanceId().equals(new ObisCode(obisCode))) {
        scalerUnitIndex = index;
      }
      index++;
    }

    // Get scaler unit from result list. Note: "index + 1" because the first result is the array
    // with values
    // and should be skipped. The first scaler unit is at index 1.
    if (scalerUnitIndex != null) {
      return getResultList.get(scalerUnitIndex + 1).getResultData();
    }

    return null;
  }

  private Date readCaptureTime(
      final ConversionContext ctx, final Optional<Date> previousCaptureTime)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final List<DataObject> bufferedObjects = ctx.bufferedObjects;
    final AttributeAddressForProfile attributeAddressForProfile = ctx.attributeAddressForProfile;

    final Integer captureTimeIndex =
        attributeAddressForProfile.getIndex(DlmsObjectType.MBUS_MASTER_VALUE, 5);

    if (captureTimeIndex != null) {
      final CosemDateTimeDto cosemDateTime =
          this.dlmsHelper.readDateTime(
              bufferedObjects.get(captureTimeIndex), "Clock from mbus interval extended register");

      if (cosemDateTime != null) {
        if (cosemDateTime.isDateTimeSpecified()) {
          return cosemDateTime.asDateTime().toDate();
        } else {
          throw new ProtocolAdapterException(UNEXPECTED_VALUE);
        }
      } else {
        return this.calculateIntervalTimeBasedOnPreviousValue(
            ctx.periodicMeterReadsQuery.getPeriodType(), previousCaptureTime, ctx.intervalTime);
      }
    }

    return null;
  }

  private AttributeAddressForProfile getProfileBufferAddress(
      final PeriodTypeDto periodType,
      final ChannelDto channel,
      final DateTime beginDateTime,
      final DateTime endDateTime,
      final DlmsDevice device)
      throws ProtocolAdapterException {

    final DlmsObjectType type = DlmsObjectType.getTypeForPeriodType(periodType);

    // Add the attribute address for the profile
    final AttributeAddressForProfile attributeAddressProfile =
        this.dlmsObjectConfigService
            .findAttributeAddressForProfile(
                device, type, channel.getChannelNumber(), beginDateTime, endDateTime, Medium.GAS)
            .orElseThrow(() -> new ProtocolAdapterException("No address found for " + type));

    LOGGER.debug(
        "Dlms object config service returned profile buffer address {} ", attributeAddressProfile);

    return attributeAddressProfile;
  }

  private List<AttributeAddress> getScalerUnitAddresses(
      final ChannelDto channel, final AttributeAddressForProfile attributeAddressForProfile) {

    final List<AttributeAddress> attributeAddresses =
        this.dlmsObjectConfigService.getAttributeAddressesForScalerUnit(
            attributeAddressForProfile, channel.getChannelNumber());

    LOGGER.debug(
        "Dlms object config service returned scaler unit addresses {} ", attributeAddresses);

    return attributeAddresses;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }
}
