// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DAILY_VALUES_E;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.INTERVAL_VALUES_E;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MONTHLY_VALUES_E;

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
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.Medium;
import org.opensmartgridplatform.dlms.objectconfig.ProfileCaptureTime;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseWithLogTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class GetPeriodicMeterReadsCommandExecutor
    extends AbstractPeriodicMeterReadsCommandExecutor<
        PeriodicMeterReadsRequestDto, PeriodicMeterReadsResponseDto> {

  private static final String ELECTRICITY_VALUE = "electricityValue";
  private static final String PERIODIC_E_METER_READS = "Periodic E-Meter Reads";
  private static final String FORMAT_DESCRIPTION =
      "GetPeriodicMeterReads %s from %s until %s, retrieve attribute: " + "%s";

  private static final PeriodicMeterReadsConfig CONFIG =
      new PeriodicMeterReadsConfig(
          PERIODIC_E_METER_READS,
          FORMAT_DESCRIPTION,
          Medium.ELECTRICITY,
          INTERVAL_VALUES_E,
          DAILY_VALUES_E,
          MONTHLY_VALUES_E);

  private final DlmsHelper dlmsHelper;

  public GetPeriodicMeterReadsCommandExecutor(
      final DlmsHelper dlmsHelper,
      final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
      final ObjectConfigService objectConfigService) {
    super(
        PeriodicMeterReadsRequestDataDto.class,
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

    final List<MeterReadsResponseWithLogTimeDto> periodicMeterReads =
        this.getPeriodicMeterReads(conn, device, periodicMeterReadsQuery, messageMetadata);

    final List<PeriodicMeterReadsResponseItemDto> periodicElectricityMeterReads =
        periodicMeterReads.stream().map(r -> (PeriodicMeterReadsResponseItemDto) r).toList();

    return new PeriodicMeterReadsResponseDto(
        periodicMeterReadsQuery.getPeriodType(), periodicElectricityMeterReads);
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
    AmrProfileStatusCodeDto status = null;
    DlmsMeterValueDto activeEnergyImport = null;
    DlmsMeterValueDto activeEnergyExport = null;
    DlmsMeterValueDto activeEnergyImportRate1 = null;
    DlmsMeterValueDto activeEnergyImportRate2 = null;
    DlmsMeterValueDto activeEnergyExportRate1 = null;
    DlmsMeterValueDto activeEnergyExportRate2 = null;

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
                AMR_PROFILE_STATUS_15MIN_E,
                AMR_PROFILE_STATUS_DAILY_E,
                AMR_PROFILE_STATUS_MONTHLY_E ->
            // The status is used in most profiles. But for some it is not used. In that case, the
            // selectedObjects will not contain a status object and readStatus will return null.
            status = this.readAmrProfileStatusCode(bufferedObject);

        case ACTIVE_ENERGY_IMPORT ->
            activeEnergyImport = this.getValue(selectedObject, bufferedObject);
        case ACTIVE_ENERGY_EXPORT ->
            activeEnergyExport = this.getValue(selectedObject, bufferedObject);
        case ACTIVE_ENERGY_IMPORT_RATE_1 ->
            activeEnergyImportRate1 = this.getValue(selectedObject, bufferedObject);
        case ACTIVE_ENERGY_IMPORT_RATE_2 ->
            activeEnergyImportRate2 = this.getValue(selectedObject, bufferedObject);
        case ACTIVE_ENERGY_EXPORT_RATE_1 ->
            activeEnergyExportRate1 = this.getValue(selectedObject, bufferedObject);
        case ACTIVE_ENERGY_EXPORT_RATE_2 ->
            activeEnergyExportRate2 = this.getValue(selectedObject, bufferedObject);
        case MBUS_MASTER_VALUE -> {
          // Do nothing. Special case for DSMR4.2.2 devices where E-meter and G-meter readings
          // are combined in one profile. G-meter readings are ignored.
        }
        default ->
            log.error(
                "Unexpected objectType in selectedObjects: "
                    + selectedObject.getCosemObject().getTag());
      }
    }
    log.debug(
        "Resulting values: LogTime: {}, status: {}, importValue {}, exportValue {}, "
            + "importRate1Value {}, importRate2Value {}, exportRate1Value {}, exportRate2Value {} ",
        logTime,
        status,
        activeEnergyImport,
        activeEnergyExport,
        activeEnergyImportRate1,
        activeEnergyImportRate2,
        activeEnergyExportRate1,
        activeEnergyExportRate2);

    return new PeriodicMeterReadsResponseItemDto(
        logTime,
        new ActiveEnergyValuesDto(
            activeEnergyImport,
            activeEnergyExport,
            activeEnergyImportRate1,
            activeEnergyImportRate2,
            activeEnergyExportRate1,
            activeEnergyExportRate2),
        status);
  }

  private DlmsMeterValueDto getValue(final CaptureObject register, final DataObject bufferedObjects)
      throws ProtocolAdapterException {
    // The meter values have no information about the scaler or the unit, so that information
    // is retrieved from the corresponding capture object in the selected objects.
    return this.dlmsHelper.getScaledMeterValueWithScalerUnit(
        bufferedObjects, ((Register) register.getCosemObject()).getScalerUnit(), ELECTRICITY_VALUE);
  }
}
