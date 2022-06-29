/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.domain.smartmetering.config.ValueType.FIXED_IN_PROFILE;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.ACTIVE_ENERGY_EXPORT;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.ACTIVE_ENERGY_IMPORT;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2;
import static org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType.CLOCK;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.domain.smartmetering.config.Attribute;
import org.opensmartgridplatform.domain.smartmetering.config.CosemObject;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectService;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetActualMeterReadsCommandExecutor
    extends AbstractCommandExecutor<ActualMeterReadsQueryDto, MeterReadsResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetActualMeterReadsCommandExecutor.class);

  @Autowired private DlmsHelper dlmsHelper;

  @Autowired private DlmsObjectService dlmsObjectService;

  public GetActualMeterReadsCommandExecutor() {
    super(ActualMeterReadsDataDto.class);
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

    final Map<DlmsObjectType, CosemObject> cosemObjects = this.getCosemObjects(device);
    final AttributeAddress[] addresses = this.getAttributeAddresses(cosemObjects);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReads retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(addresses));

    LOGGER.info("Retrieving actual energy reads");
    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(conn, device, "retrieve actual meter reads", addresses);

    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(
            this.getResult(getResultList, cosemObjects, CLOCK, addresses),
            "Actual Energy Reads Time");
    final DateTime time = cosemDateTime.asDateTime();
    if (time == null) {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for Actual Energy Reads Time");
    }
    final DlmsMeterValueDto activeEnergyImport =
        this.dlmsHelper.getScaledMeterValue(
            this.getResult(getResultList, cosemObjects, ACTIVE_ENERGY_IMPORT, addresses),
            this.getScalerUnit(getResultList, cosemObjects, ACTIVE_ENERGY_IMPORT, addresses),
            "Actual Energy Reads +A");
    final DlmsMeterValueDto activeEnergyExport =
        this.dlmsHelper.getScaledMeterValue(
            this.getResult(getResultList, cosemObjects, ACTIVE_ENERGY_EXPORT, addresses),
            this.getScalerUnit(getResultList, cosemObjects, ACTIVE_ENERGY_EXPORT, addresses),
            "Actual Energy Reads -A");
    final DlmsMeterValueDto activeEnergyImportRate1 =
        this.dlmsHelper.getScaledMeterValue(
            this.getResult(getResultList, cosemObjects, ACTIVE_ENERGY_IMPORT_RATE_1, addresses),
            this.getScalerUnit(getResultList, cosemObjects, ACTIVE_ENERGY_IMPORT_RATE_1, addresses),
            "Actual Energy Reads +A rate 1");
    final DlmsMeterValueDto activeEnergyImportRate2 =
        this.dlmsHelper.getScaledMeterValue(
            this.getResult(getResultList, cosemObjects, ACTIVE_ENERGY_IMPORT_RATE_2, addresses),
            this.getScalerUnit(getResultList, cosemObjects, ACTIVE_ENERGY_IMPORT_RATE_2, addresses),
            "Actual Energy Reads +A rate 2");
    final DlmsMeterValueDto activeEnergyExportRate1 =
        this.dlmsHelper.getScaledMeterValue(
            this.getResult(getResultList, cosemObjects, ACTIVE_ENERGY_EXPORT_RATE_1, addresses),
            this.getScalerUnit(getResultList, cosemObjects, ACTIVE_ENERGY_EXPORT_RATE_1, addresses),
            "Actual Energy Reads -A rate 1");
    final DlmsMeterValueDto activeEnergyExportRate2 =
        this.dlmsHelper.getScaledMeterValue(
            this.getResult(getResultList, cosemObjects, ACTIVE_ENERGY_EXPORT_RATE_2, addresses),
            this.getScalerUnit(getResultList, cosemObjects, ACTIVE_ENERGY_EXPORT_RATE_2, addresses),
            "Actual Energy Reads -A rate 2");

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

  private Map<DlmsObjectType, CosemObject> getCosemObjects(final DlmsDevice device) {
    final Map<DlmsObjectType, CosemObject> objectMap =
        this.dlmsObjectService.getCosemObjects(
            device.getProtocolName(), device.getProtocolVersion());

    if (objectMap.isEmpty()) {
      LOGGER.error(
          "CosemObject configuration is not found for device protocol {} and version {}",
          device.getProtocolName(),
          device.getProtocolVersion());
    }

    return objectMap;
  }

  private AttributeAddress[] getAttributeAddresses(
      final Map<DlmsObjectType, CosemObject> objectMap) {
    return objectMap.values().stream()
        .map(this::getAttributeAddressForObject)
        .flatMap(List::stream)
        .toArray(AttributeAddress[]::new);
  }

  private List<AttributeAddress> getAttributeAddressForObject(final CosemObject object) {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();

    attributeAddresses.add(new AttributeAddress(object.classId, object.obis, 2));

    if (object.classId == InterfaceClass.REGISTER.id()) {
      final Attribute scalerUnit = object.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());
      if (!scalerUnit.valuetype.equals(FIXED_IN_PROFILE)) {
        attributeAddresses.add(
            new AttributeAddress(
                object.classId, object.obis, RegisterAttribute.SCALER_UNIT.attributeId()));
      }
    }

    return attributeAddresses;
  }

  private int getIndex(
      final String obis, final int attributeId, final AttributeAddress[] attributeAddresses) {

    for (int i = 0; i < attributeAddresses.length; i++) {
      if (attributeAddresses[i].getInstanceId().asDecimalString().equals(obis)
          && attributeAddresses[i].getId() == attributeId) {
        return i;
      }
    }

    return 0;
  }

  private DataObject getResult(
      final List<GetResult> getResultList,
      final Map<DlmsObjectType, CosemObject> objectMap,
      final DlmsObjectType type,
      final AttributeAddress[] attributeAddresses) {

    final CosemObject cosemObject = objectMap.get(type);
    final int index = this.getIndex(cosemObject.obis, 2, attributeAddresses);
    return getResultList.get(index).getResultData();
  }

  private DataObject getScalerUnit(
      final List<GetResult> getResultList,
      final Map<DlmsObjectType, CosemObject> objectMap,
      final DlmsObjectType type,
      final AttributeAddress[] attributeAddresses) {

    final CosemObject cosemObject = objectMap.get(type);

    final Attribute scalerUnit =
        cosemObject.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());
    if (!scalerUnit.valuetype.equals(FIXED_IN_PROFILE)) {
      final int index =
          this.getIndex(
              cosemObject.obis, RegisterAttribute.SCALER_UNIT.attributeId(), attributeAddresses);
      return getResultList.get(index + 1).getResultData();
    } else {
      final String scalerUnitString = scalerUnit.value;
      return DataObject.newStructureData(
          DataObject.newInteger32Data(this.getScaler(scalerUnitString)),
          DataObject.newInteger32Data(this.getUnit(scalerUnitString).getIndex()));
    }
  }

  private int getScaler(final String scaler_unit) {
    return Integer.parseInt(scaler_unit.split(",")[0]);
  }

  private DlmsUnitTypeDto getUnit(final String scaler_unit) throws IllegalArgumentException {
    final String unit = scaler_unit.split(",")[1].trim();

    switch (unit) {
      case "Wh":
        return DlmsUnitTypeDto.KWH; // TODO: There is no Wh!
      case "V":
        return DlmsUnitTypeDto.VOLT;
      default:
        throw new IllegalArgumentException("Unknown unit in profile: " + unit);
    }
  }
}
