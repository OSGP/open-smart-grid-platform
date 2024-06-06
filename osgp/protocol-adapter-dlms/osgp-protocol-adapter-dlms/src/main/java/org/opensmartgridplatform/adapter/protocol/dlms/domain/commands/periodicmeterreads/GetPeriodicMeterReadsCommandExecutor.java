// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPeriodicMeterReadsCommandExecutor
    extends AbstractPeriodicMeterReadsCommandExecutor<
        PeriodicMeterReadsRequestDto, PeriodicMeterReadsResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

  static final String PERIODIC_E_METER_READS = "Periodic E-Meter Reads";
  private static final String FORMAT_DESCRIPTION =
      "GetPeriodicMeterReads %s from %s until %s, retrieve attribute: " + "%s";

  private final DlmsHelper dlmsHelper;
  private final DlmsObjectConfigService dlmsObjectConfigService;

  @Autowired
  public GetPeriodicMeterReadsCommandExecutor(
      final DlmsHelper dlmsHelper,
      final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
      final DlmsObjectConfigService dlmsObjectConfigService) {
    super(PeriodicMeterReadsRequestDataDto.class, amrProfileStatusCodeHelper);
    this.dlmsHelper = dlmsHelper;
    this.dlmsObjectConfigService = dlmsObjectConfigService;
  }

  @Override
  public PeriodicMeterReadsRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final PeriodicMeterReadsRequestDataDto periodicMeterReadsRequestDataDto =
        (PeriodicMeterReadsRequestDataDto) bundleInput;

    return new PeriodicMeterReadsRequestDto(
        periodicMeterReadsRequestDataDto.getPeriodType(),
        periodicMeterReadsRequestDataDto.getBeginDate(),
        periodicMeterReadsRequestDataDto.getEndDate());
  }

  @Override
  public PeriodicMeterReadsResponseDto execute(
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
            queryPeriodType, from, to, device, this.dlmsObjectConfigService, Medium.ELECTRICITY, 0);

    final List<AttributeAddress> scalerUnitAddresses =
        this.getScalerUnitAddresses(profileBufferAddress);

    final Optional<ProfileCaptureTime> intervalTime =
        this.getProfileCaptureTime(device, this.dlmsObjectConfigService, Medium.ELECTRICITY);

    LOGGER.debug(
        "Retrieving current billing period and profiles for period type: {}, from: {}, to: {}",
        queryPeriodType,
        from,
        to);

    // Get results one by one because getWithList does not work for all devices
    final List<GetResult> getResultList = new ArrayList<>();

    final List<AttributeAddress> allAttributeAddresses = new ArrayList<>();
    allAttributeAddresses.add(profileBufferAddress.getAttributeAddress());
    allAttributeAddresses.addAll(scalerUnitAddresses);

    for (final AttributeAddress address : allAttributeAddresses) {

      conn.getDlmsMessageListener()
          .setDescription(
              String.format(
                  FORMAT_DESCRIPTION,
                  queryPeriodType,
                  from,
                  to,
                  JdlmsObjectToStringUtil.describeAttributes(address)));

      getResultList.addAll(
          this.dlmsHelper.getAndCheck(
              conn, device, "retrieve periodic meter reads for " + queryPeriodType, address));
    }

    LOGGER.debug("Received getResult: {} ", getResultList);

    final DataObject resultData =
        this.dlmsHelper.readDataObject(getResultList.get(0), PERIODIC_E_METER_READS);
    final List<DataObject> bufferedObjectsList = resultData.getValue();

    final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = new ArrayList<>();
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

    final List<PeriodicMeterReadsResponseItemDto> periodicMeterReadsWithinRequestedPeriod =
        periodicMeterReads.stream()
            .filter(
                meterRead ->
                    this.validateDateTime(meterRead.getLogTime(), from.toDate(), to.toDate()))
            .toList();

    return new PeriodicMeterReadsResponseDto(
        queryPeriodType, periodicMeterReadsWithinRequestedPeriod);
  }

  private PeriodicMeterReadsResponseItemDto convertToResponseItem(
      final ConversionContext ctx, final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    LOGGER.debug("Converting bufferObject with value: {} ", ctx.bufferedObjects);

    final Optional<Date> previousLogTime = this.getPreviousLogTime(periodicMeterReads);
    final Date logTime = this.readClock(ctx, previousLogTime, this.dlmsHelper);

    final AmrProfileStatusCodeDto status =
        this.readStatus(ctx.bufferedObjects, ctx.attributeAddressForProfile);

    if (ctx.periodicMeterReadsQuery.getPeriodType() == PeriodTypeDto.INTERVAL) {
      final DlmsMeterValueDto importValue =
          this.getScaledMeterValue(
              ctx.bufferedObjects,
              ctx.getResultList,
              ctx.attributeAddresses,
              ctx.attributeAddressForProfile,
              DlmsObjectType.ACTIVE_ENERGY_IMPORT,
              "positiveActiveEnergy");
      final DlmsMeterValueDto exportValue =
          this.getScaledMeterValue(
              ctx.bufferedObjects,
              ctx.getResultList,
              ctx.attributeAddresses,
              ctx.attributeAddressForProfile,
              DlmsObjectType.ACTIVE_ENERGY_EXPORT,
              "negativeActiveEnergy");

      LOGGER.debug(
          "Resulting values: LogTime: {}, status: {}, importValue {}, exportValue {} ",
          logTime,
          status,
          importValue,
          exportValue);

      return new PeriodicMeterReadsResponseItemDto(logTime, importValue, exportValue, status);
    } else {
      final DlmsMeterValueDto importValueRate1 =
          this.getScaledMeterValue(
              ctx.bufferedObjects,
              ctx.getResultList,
              ctx.attributeAddresses,
              ctx.attributeAddressForProfile,
              DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1,
              "positiveActiveEnergyTariff1");
      final DlmsMeterValueDto importValueRate2 =
          this.getScaledMeterValue(
              ctx.bufferedObjects,
              ctx.getResultList,
              ctx.attributeAddresses,
              ctx.attributeAddressForProfile,
              DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2,
              "positiveActiveEnergyTariff2");
      final DlmsMeterValueDto exportValueRate1 =
          this.getScaledMeterValue(
              ctx.bufferedObjects,
              ctx.getResultList,
              ctx.attributeAddresses,
              ctx.attributeAddressForProfile,
              DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1,
              "negativeActiveEnergyTariff1");
      final DlmsMeterValueDto exportValueRate2 =
          this.getScaledMeterValue(
              ctx.bufferedObjects,
              ctx.getResultList,
              ctx.attributeAddresses,
              ctx.attributeAddressForProfile,
              DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2,
              "negativeActiveEnergyTariff2");

      LOGGER.debug(
          "Resulting values: LogTime: {}, status: {}, importRate1Value {}, importRate2Value {}, "
              + "exportRate1Value {}, exportRate2Value {} ",
          logTime,
          status,
          importValueRate1,
          importValueRate2,
          exportValueRate1,
          exportValueRate2);

      return new PeriodicMeterReadsResponseItemDto(
          logTime, importValueRate1, importValueRate2, exportValueRate1, exportValueRate2, status);
    }
  }

  private Optional<Date> getPreviousLogTime(
      final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads) {

    if (periodicMeterReads.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(periodicMeterReads.get(periodicMeterReads.size() - 1).getLogTime());
  }

  private DlmsMeterValueDto getScaledMeterValue(
      final List<DataObject> bufferedObjects,
      final List<GetResult> getResultList,
      final List<AttributeAddress> attributeAddresses,
      final AttributeAddressForProfile attributeAddressForProfile,
      final DlmsObjectType objectType,
      final String description)
      throws ProtocolAdapterException {

    final DataObject importValue =
        this.readValue(bufferedObjects, attributeAddressForProfile, objectType);
    final DataObject importScalerUnit =
        this.readScalerUnit(
            getResultList, attributeAddresses, attributeAddressForProfile, objectType);

    return this.dlmsHelper.getScaledMeterValue(importValue, importScalerUnit, description);
  }

  private DataObject readValue(
      final List<DataObject> bufferedObjects,
      final AttributeAddressForProfile attributeAddressForProfile,
      final DlmsObjectType objectType) {

    final Integer valueIndex = attributeAddressForProfile.getIndex(objectType, 2);

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
      final DlmsObjectType objectType)
      throws ProtocolAdapterException {

    final DlmsCaptureObject captureObject = attributeAddressForProfile.getCaptureObject(objectType);

    int index = 0;
    Integer scalerUnitIndex = null;
    for (final AttributeAddress address : attributeAddresses) {
      final ObisCode obisCode = captureObject.getRelatedObject().getObisCode();
      if (address.getInstanceId().equals(obisCode)) {
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

  private List<AttributeAddress> getScalerUnitAddresses(
      final AttributeAddressForProfile attributeAddressForProfile) {

    final List<AttributeAddress> attributeAddresses =
        this.dlmsObjectConfigService.getAttributeAddressesForScalerUnit(
            attributeAddressForProfile, 0);

    LOGGER.debug(
        "Dlms object config service returned scaler unit addresses {} ", attributeAddresses);

    return attributeAddresses;
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }
}
