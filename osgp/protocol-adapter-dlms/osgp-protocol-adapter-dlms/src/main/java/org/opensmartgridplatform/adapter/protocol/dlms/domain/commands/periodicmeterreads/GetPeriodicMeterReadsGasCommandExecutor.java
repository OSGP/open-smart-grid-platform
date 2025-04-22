// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.CAPTURE_TIME;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute.VALUE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DAILY_VALUES_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.INTERVAL_VALUES_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MONTHLY_VALUES_G;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.Medium;
import org.opensmartgridplatform.dlms.objectconfig.ProfileCaptureTime;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseWithLogTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class GetPeriodicMeterReadsGasCommandExecutor
    extends AbstractPeriodicMeterReadsCommandExecutor<
        PeriodicMeterReadsRequestDto, PeriodicMeterReadGasResponseDto> {

  private static final String GAS_VALUE = "gasValue";
  private static final String PERIODIC_G_METER_READS = "Periodic G-Meter Reads";
  private static final String UNEXPECTED_VALUE =
      "Unexpected null/unspecified value for Gas Capture Time";
  private static final String FORMAT_DESCRIPTION =
      "GetPeriodicMeterReadsGas %s from %s until %s, " + "retrieve attribute: %s, channel %s";

  private static final PeriodicMeterReadsConfig CONFIG =
      new PeriodicMeterReadsConfig(
          PERIODIC_G_METER_READS,
          FORMAT_DESCRIPTION,
          Medium.GAS,
          INTERVAL_VALUES_G,
          DAILY_VALUES_G,
          MONTHLY_VALUES_G);

  private final DlmsHelper dlmsHelper;

  public GetPeriodicMeterReadsGasCommandExecutor(
      final DlmsHelper dlmsHelper,
      final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
      final ObjectConfigService objectConfigService) {

    super(
        PeriodicMeterReadsGasRequestDto.class,
        amrProfileStatusCodeHelper,
        dlmsHelper,
        objectConfigService,
        CONFIG);
    this.dlmsHelper = dlmsHelper;
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
    final List<MeterReadsResponseWithLogTimeDto> periodicMeterReads =
        this.getPeriodicMeterReads(conn, device, periodicMeterReadsQuery, messageMetadata);

    final List<PeriodicMeterReadsGasResponseItemDto> periodicGasMeterReads =
        periodicMeterReads.stream().map(r -> (PeriodicMeterReadsGasResponseItemDto) r).toList();

    return new PeriodicMeterReadGasResponseDto(
        periodicMeterReadsQuery.getPeriodType(), periodicGasMeterReads);
  }

  @Override
  protected MeterReadsResponseWithLogTimeDto convertToResponseItem(
      final PeriodTypeDto periodType,
      final List<CaptureObject> selectedObjects,
      final ProfileCaptureTime intervalTime,
      final List<DataObject> bufferedObjects,
      final int channel,
      final List<MeterReadsResponseWithLogTimeDto> periodicMeterReads)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    log.debug("Converting bufferObject with value: {} ", bufferedObjects);

    // The bufferedObjects contain the values retrieved from the meter for a single interval.
    // The bufferedObjects contain no information about the type of value. But because the
    // bufferedObjects are always in the same known order (the order of the selectedObjects), we can
    // still convert the values.

    Date logTime = null;
    Date captureTime = null;
    AmrProfileStatusCodeDto status = null;
    DlmsMeterValueDto gasValue = null;

    for (int index = 0; index < selectedObjects.size(); index++) {
      final CaptureObject selectedObject = selectedObjects.get(index);

      final DataObject bufferedObject = bufferedObjects.get(index);

      switch (DlmsObjectType.valueOf(selectedObject.getCosemObject().getTag())) {
        case CLOCK -> {
          // The first timestamp in the response of a meter should always be included. The following
          // intervals could have a 'null' timestamp, meaning the time should be calculated based on
          // the previous timestamp.
          final Optional<Date> previousLogTime = this.getPreviousLogTime(periodicMeterReads);
          logTime = this.readClock(periodType, previousLogTime, intervalTime, bufferedObject);
        }
        case AMR_PROFILE_STATUS,
                AMR_PROFILE_STATUS_HOURLY_G,
                AMR_PROFILE_STATUS_DAILY_G,
                AMR_PROFILE_STATUS_MONTHLY_G ->
            // The status is used in most profiles. But for some it is not used. In that case, the
            // selectedObjects will not contain a status object and readStatus will return null.
            status = this.readAmrProfileStatusCode(bufferedObject);

        case MBUS_MASTER_VALUE -> {
          // The gasValue should always be included. Values for other channels can be ignored.
          // The value of the meter has no information about the scaler or the unit, so that
          // information is retrieved from the corresponding capture object in the selected
          // objects.
          if (this.getChannel(selectedObject) == channel) {
            if (selectedObject.getAttributeId() == VALUE.attributeId()) {
              gasValue =
                  this.dlmsHelper.getScaledMeterValueWithScalerUnit(
                      bufferedObject,
                      ((Register) selectedObject.getCosemObject()).getScalerUnit(),
                      GAS_VALUE);
            } else if (selectedObject.getAttributeId() == CAPTURE_TIME.attributeId()) {
              // The capture time is used in most profiles. But for some it is not used. In that
              // case, the selectedObjects will not contain a capture time object and
              // readCaptureTime will return null.
              final Optional<Date> previousCaptureTime =
                  this.getPreviousCaptureTime(periodicMeterReads);
              captureTime =
                  this.readCaptureTime(
                      bufferedObject, previousCaptureTime, periodType, intervalTime);
            }
          }
        }
        case ACTIVE_ENERGY_IMPORT,
            ACTIVE_ENERGY_EXPORT,
            ACTIVE_ENERGY_IMPORT_RATE_1,
            ACTIVE_ENERGY_IMPORT_RATE_2,
            ACTIVE_ENERGY_EXPORT_RATE_1,
            ACTIVE_ENERGY_EXPORT_RATE_2 -> {
          // Do nothing. Special case for DSMR4.2.2 devices where E-meter and G-meter readings
          // are combined in one profile. E-meter readings are ignored.
        }
        default ->
            log.error(
                "Unexpected objectType in selectedObjects: "
                    + selectedObject.getCosemObject().getTag());
      }
    }

    log.debug(
        "Resulting values: LogTime: {}, status: {}, gasValue {}, captureTime {} ",
        logTime,
        status,
        gasValue,
        captureTime);

    return new PeriodicMeterReadsGasResponseItemDto(logTime, gasValue, captureTime, status);
  }

  private Optional<Date> getPreviousCaptureTime(
      final List<MeterReadsResponseWithLogTimeDto> periodicMeterReads) {

    if (periodicMeterReads.isEmpty()) {
      return Optional.empty();
    }

    final MeterReadsResponseWithLogTimeDto meterRead =
        periodicMeterReads.get(periodicMeterReads.size() - 1);

    final Date previousCaptureTime =
        ((PeriodicMeterReadsGasResponseItemDto) meterRead).getCaptureTime();

    if (previousCaptureTime == null) {
      return Optional.empty();
    } else {
      return Optional.of(previousCaptureTime);
    }
  }

  private int getChannel(final CaptureObject object) throws ProtocolAdapterException {
    try {
      return object.getCosemObject().getChannel();
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Unable to get channel from selected object", e);
    }
  }

  private Date readCaptureTime(
      final DataObject bufferedObject,
      final Optional<Date> previousCaptureTime,
      final PeriodTypeDto periodType,
      final ProfileCaptureTime intervalTime)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(bufferedObject, "Clock from mbus interval extended register");

    if (cosemDateTime != null) {
      if (cosemDateTime.isDateTimeSpecified()) {
        return cosemDateTime.asDateTime().toDate();
      } else {
        throw new ProtocolAdapterException(UNEXPECTED_VALUE);
      }
    } else {
      return this.calculateIntervalTimeBasedOnPreviousValue(
          periodType, previousCaptureTime, Optional.of(intervalTime));
    }
  }
}
