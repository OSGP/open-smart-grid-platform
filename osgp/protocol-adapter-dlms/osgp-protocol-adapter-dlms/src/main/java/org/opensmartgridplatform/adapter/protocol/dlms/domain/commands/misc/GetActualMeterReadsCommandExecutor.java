// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.CLOCK;

import java.util.List;
import org.joda.time.DateTime;
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
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class GetActualMeterReadsCommandExecutor
    extends AbstractCommandExecutor<ActualMeterReadsQueryDto, MeterReadsResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetActualMeterReadsCommandExecutor.class);

  private static final int CLASS_ID_REGISTER = 3;
  private static final byte ATTRIBUTE_ID_VALUE = 2;

  private static final int CLASS_ID_CLOCK = 8;
  private static final byte ATTRIBUTE_ID_TIME = 2;

  private static final int INDEX_TIME = 0;
  private static final int INDEX_IMPORT = 1;
  private static final int INDEX_IMPORT_RATE_1 = 2;
  private static final int INDEX_IMPORT_RATE_2 = 3;
  private static final int INDEX_EXPORT = 4;
  private static final int INDEX_EXPORT_RATE_1 = 5;
  private static final int INDEX_EXPORT_RATE_2 = 6;

  private final DlmsHelper dlmsHelper;

  private final ObjectConfigService objectConfigService;

  public GetActualMeterReadsCommandExecutor(
      final ObjectConfigService objectConfigService, final DlmsHelper dlmsHelper) {
    super(ActualMeterReadsDataDto.class);

    this.objectConfigService = objectConfigService;
    this.dlmsHelper = dlmsHelper;
  }

  @Override
  public ActualMeterReadsQueryDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    /*
     * The ActionRequestDto, which is an ActualMeterReadsDataDto does not
     * contain any data, so no further configuration of the
     * ActualMeterReadsQueryDto is necessary.
     */
    return new ActualMeterReadsQueryDto();
  }

  @Override
  public MeterReadsResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualMeterReadsQueryDto actualMeterReadsQuery,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    if (actualMeterReadsQuery != null && actualMeterReadsQuery.isMbusQuery()) {
      throw new IllegalArgumentException(
          "ActualMeterReadsQuery object for energy reads should not be about gas.");
    }

    final CosemObject clockObject;
    final Register importObject;
    final Register importRate1Object;
    final Register importRate2Object;
    final Register exportObject;
    final Register exportRate1Object;
    final Register exportRate2Object;
    try {
      clockObject = this.getCosemObject(device, CLOCK);
      importObject = (Register) this.getCosemObject(device, ACTIVE_ENERGY_IMPORT);
      importRate1Object = (Register) this.getCosemObject(device, ACTIVE_ENERGY_IMPORT_RATE_1);
      importRate2Object = (Register) this.getCosemObject(device, ACTIVE_ENERGY_IMPORT_RATE_2);
      exportObject = (Register) this.getCosemObject(device, ACTIVE_ENERGY_EXPORT);
      exportRate1Object = (Register) this.getCosemObject(device, ACTIVE_ENERGY_EXPORT_RATE_1);
      exportRate2Object = (Register) this.getCosemObject(device, ACTIVE_ENERGY_EXPORT_RATE_2);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }

    final AttributeAddress[] atttributeAddresses = {
      new AttributeAddress(CLASS_ID_CLOCK, clockObject.getObis(), ATTRIBUTE_ID_TIME),
      new AttributeAddress(CLASS_ID_REGISTER, importObject.getObis(), ATTRIBUTE_ID_VALUE),
      new AttributeAddress(CLASS_ID_REGISTER, importRate1Object.getObis(), ATTRIBUTE_ID_VALUE),
      new AttributeAddress(CLASS_ID_REGISTER, importRate2Object.getObis(), ATTRIBUTE_ID_VALUE),
      new AttributeAddress(CLASS_ID_REGISTER, exportObject.getObis(), ATTRIBUTE_ID_VALUE),
      new AttributeAddress(CLASS_ID_REGISTER, exportRate1Object.getObis(), ATTRIBUTE_ID_VALUE),
      new AttributeAddress(CLASS_ID_REGISTER, exportRate2Object.getObis(), ATTRIBUTE_ID_VALUE),
    };

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReads retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(atttributeAddresses));

    LOGGER.debug("Retrieving actual energy reads");
    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual meter reads", atttributeAddresses);

    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(getResultList.get(INDEX_TIME), "Actual Energy Reads Time");
    final DateTime time = cosemDateTime.asDateTime();
    if (time == null) {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for Actual Energy Reads Time");
    }
    final DlmsMeterValueDto activeEnergyImport =
        this.getValue(getResultList.get(INDEX_IMPORT), importObject, "+A");
    final DlmsMeterValueDto activeEnergyExport =
        this.getValue(getResultList.get(INDEX_EXPORT), exportObject, "-A");
    final DlmsMeterValueDto activeEnergyImportRate1 =
        this.getValue(getResultList.get(INDEX_IMPORT_RATE_1), importRate1Object, "+A rate 1");
    final DlmsMeterValueDto activeEnergyImportRate2 =
        this.getValue(getResultList.get(INDEX_IMPORT_RATE_2), importRate2Object, "+A rate 2");
    final DlmsMeterValueDto activeEnergyExportRate1 =
        this.getValue(getResultList.get(INDEX_EXPORT_RATE_1), exportRate1Object, "-A rate 1");
    final DlmsMeterValueDto activeEnergyExportRate2 =
        this.getValue(getResultList.get(INDEX_EXPORT_RATE_2), exportRate2Object, "-A rate 2");

    return new MeterReadsResponseDto(
        time.toDate(),
        new ActiveEnergyValuesDto(
            activeEnergyImport,
            activeEnergyExport,
            activeEnergyImportRate1,
            activeEnergyImportRate2,
            activeEnergyExportRate1,
            activeEnergyExportRate2));
  }

  private CosemObject getCosemObject(final DlmsDevice device, final DlmsObjectType objectType)
      throws ObjectConfigException {
    return this.objectConfigService.getCosemObject(
        device.getProtocolName(), device.getProtocolVersion(), objectType);
  }

  private DlmsMeterValueDto getValue(
      final GetResult getResult, final Register register, final String description)
      throws ProtocolAdapterException {
    return this.dlmsHelper.getScaledMeterValueWithScalerUnit(
        getResult, register.getScalerUnit(), "Actual Energy Reads " + description);
  }
}
