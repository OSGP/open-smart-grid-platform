/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPeriodicMeterReadsCommandExecutor<T, R>
    extends AbstractCommandExecutor<T, R> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractPeriodicMeterReadsCommandExecutor.class);
  private final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;

  AbstractPeriodicMeterReadsCommandExecutor(
      final Class<? extends PeriodicMeterReadsRequestDataDto> clazz,
      final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper) {
    super(clazz);
    this.amrProfileStatusCodeHelper = amrProfileStatusCodeHelper;
  }

  /**
   * Calculates/derives the date of the read buffered DataObject.
   *
   * @param ctx context elements for the buffered object conversion
   * @param previousLogTime the log time of the previous meter read
   * @param dlmsHelper dlms helper object
   * @return the date of the buffered {@link DataObject} or null if it cannot be determined
   * @throws ProtocolAdapterException
   * @throws BufferedDateTimeValidationException in case the date is invalid or null
   */
  Date readClock(
      final ConversionContext ctx,
      final Optional<Date> previousLogTime,
      final DlmsHelper dlmsHelper)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final Date logTime;

    final PeriodTypeDto queryPeriodType = ctx.periodicMeterReadsQuery.getPeriodType();
    final Integer clockIndex = ctx.attributeAddressForProfile.getIndex(DlmsObjectType.CLOCK, null);

    CosemDateTimeDto cosemDateTime = null;

    if (clockIndex != null) {
      cosemDateTime =
          dlmsHelper.readDateTime(
              ctx.bufferedObjects.get(clockIndex), "Clock from " + queryPeriodType + " buffer");
    }

    final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

    if (bufferedDateTime != null) {
      logTime = bufferedDateTime.toDate();
    } else {
      logTime =
          this.calculateIntervalTimeBasedOnPreviousValue(
              ctx.periodicMeterReadsQuery.getPeriodType(), previousLogTime, ctx.intervalTime);
    }

    if (logTime == null) {
      throw new BufferedDateTimeValidationException("Unable to calculate logTime");
    }

    return logTime;
  }

  /**
   * Calculates/derives the next interval time in case it was not present in the current meter read
   * record.
   *
   * @param periodTypeDto the time interval period.
   * @param previousLogTime the logTime of the previous meter read record
   * @param intervalTime the interval time for this device to be taken into account when the
   *     periodTypeDto is INTERVAL
   * @return the derived date based on the previous meter read record, or null if it cannot be
   *     determined
   */
  protected Date calculateIntervalTimeBasedOnPreviousValue(
      final PeriodTypeDto periodTypeDto,
      final Optional<Date> previousLogTime,
      final Optional<ProfileCaptureTime> intervalTime)
      throws BufferedDateTimeValidationException {

    if (!previousLogTime.isPresent()) {
      throw new BufferedDateTimeValidationException(
          "Unable to calculate next interval date, previous logTime " + "is not available");
    }

    final Date prevLogTime = previousLogTime.get();

    switch (periodTypeDto) {
      case DAILY:
        return Date.from(prevLogTime.toInstant().plus(Duration.ofDays(1)));
      case MONTHLY:
        final LocalDateTime localDateTime =
            LocalDateTime.ofInstant(prevLogTime.toInstant(), ZoneId.systemDefault()).plusMonths(1);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
      case INTERVAL:
        return Date.from(
            prevLogTime
                .toInstant()
                .plus(Duration.ofMinutes(this.getIntervalTimeMinutes(intervalTime))));
      default:
        throw new BufferedDateTimeValidationException(
            "Invalid PeriodTypeDto given: " + periodTypeDto);
    }
  }

  private int getIntervalTimeMinutes(final Optional<ProfileCaptureTime> intervalTime) {

    final ProfileCaptureTime profileCaptureTime =
        intervalTime.isPresent() ? intervalTime.get() : null;
    int intervalTimeMinutes = 0;
    if (profileCaptureTime == ProfileCaptureTime.QUARTER_HOUR) {
      intervalTimeMinutes = 15;
    } else if (profileCaptureTime == ProfileCaptureTime.HOUR) {
      intervalTimeMinutes = 60;
    }
    return intervalTimeMinutes;
  }

  /**
   * Get the interval time for given device and medium.
   *
   * @param device the device for which the interval should be determined
   * @param dlmsObjectConfigService service which holds the configuration for this device
   * @param medium the type of device
   * @return the derived ProfileCaptureTime for this device, or null if it cannot be determined
   */
  Optional<ProfileCaptureTime> getProfileCaptureTime(
      final DlmsDevice device,
      final DlmsObjectConfigService dlmsObjectConfigService,
      final Medium medium) {
    final DlmsObject dlmsObject =
        dlmsObjectConfigService
            .findDlmsObject(Protocol.forDevice(device), DlmsObjectType.INTERVAL_VALUES, medium)
            .orElse(null);

    if (dlmsObject instanceof DlmsProfile) {
      final DlmsProfile profile = (DlmsProfile) dlmsObject;

      this.getLogger().info("Capture time of this device is {} ", profile.getCaptureTime());
      return Optional.of(profile.getCaptureTime());
    }

    return Optional.empty();
  }

  AmrProfileStatusCodeDto readStatus(
      final List<DataObject> bufferedObjects,
      final AttributeAddressForProfile attributeAddressForProfile)
      throws ProtocolAdapterException {

    final Integer statusIndex =
        attributeAddressForProfile.getIndex(DlmsObjectType.AMR_STATUS, null);

    AmrProfileStatusCodeDto amrProfileStatusCode = null;

    if (statusIndex != null) {
      amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects.get(statusIndex));
    }

    return amrProfileStatusCode;
  }

  /**
   * Reads AmrProfileStatusCode from DataObject holding a bitvalue in a numeric datatype.
   *
   * @param amrProfileStatusData AMR profile register value.
   * @return AmrProfileStatusCode object holding status enum values.
   * @throws ProtocolAdapterException on invalid register data.
   */
  private AmrProfileStatusCodeDto readAmrProfileStatusCode(final DataObject amrProfileStatusData)
      throws ProtocolAdapterException {

    if (!amrProfileStatusData.isNumber()) {
      throw new ProtocolAdapterException(
          "Could not read AMR profile register data. Invalid data type.");
    }

    LOGGER.info(
        "Received amrProfileStatusData {} - {}",
        amrProfileStatusData.toString(),
        amrProfileStatusData.getValue());

    final Set<AmrProfileStatusCodeFlagDto> flags =
        this.amrProfileStatusCodeHelper.toAmrProfileStatusCodeFlags(
            amrProfileStatusData.getValue());
    return new AmrProfileStatusCodeDto(flags);
  }

  protected boolean validateDateTime(
      final Date meterReadTime, final Date beginDateTime, final Date endDateTime) {

    if (meterReadTime.before(beginDateTime) || meterReadTime.after(endDateTime)) {
      LOGGER.info(
          "Not using an object from capture buffer (clock= {}), because the date does not match the given period: [ {} .. {} ].",
          meterReadTime,
          beginDateTime,
          endDateTime);
      return false;
    } else {
      return true;
    }
  }

  protected abstract Logger getLogger();

  /** Wrapper class with items needed to convert to PeriodicMeterRead items. */
  protected static class ConversionContext {

    final PeriodicMeterReadsRequestDto periodicMeterReadsQuery;
    final List<DataObject> bufferedObjects;
    final List<GetResult> getResultList;
    final AttributeAddressForProfile attributeAddressForProfile;
    final List<AttributeAddress> attributeAddresses;
    final Optional<ProfileCaptureTime> intervalTime;

    protected ConversionContext(
        final PeriodicMeterReadsRequestDto periodicMeterReadsQuery,
        final List<DataObject> bufferedObjects,
        final List<GetResult> getResultList,
        final AttributeAddressForProfile attributeAddressForProfile,
        final List<AttributeAddress> attributeAddresses,
        final Optional<ProfileCaptureTime> intervalTime) {
      this.periodicMeterReadsQuery = periodicMeterReadsQuery;
      this.bufferedObjects = bufferedObjects;
      this.getResultList = getResultList;
      this.attributeAddressForProfile = attributeAddressForProfile;
      this.attributeAddresses = attributeAddresses;
      this.intervalTime = intervalTime;
    }
  }
}
