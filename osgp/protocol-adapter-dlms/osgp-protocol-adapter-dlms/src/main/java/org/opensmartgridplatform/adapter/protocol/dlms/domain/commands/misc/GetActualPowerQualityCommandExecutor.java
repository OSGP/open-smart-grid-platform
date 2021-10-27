/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityValueDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetActualPowerQualityCommandExecutor
    extends AbstractCommandExecutor<ActualPowerQualityRequestDto, ActualPowerQualityResponseDto> {

  private static final int CLASS_ID_REGISTER = InterfaceClass.REGISTER.id();
  private static final int CLASS_ID_DATA = InterfaceClass.DATA.id();
  private static final int CLASS_ID_CLOCK = InterfaceClass.CLOCK.id();

  private static final String PRIVATE = "PRIVATE";
  private static final String PUBLIC = "PUBLIC";

  private final DlmsHelper dlmsHelper;

  public GetActualPowerQualityCommandExecutor(final DlmsHelper dlmsHelper) {
    super(ActualPowerQualityRequestDto.class);
    this.dlmsHelper = dlmsHelper;
  }

  @Override
  public ActualPowerQualityResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualPowerQualityRequestDto actualPowerQualityRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final Profile profile = this.determineProfile(actualPowerQualityRequestDto.getProfileType());

    final AttributeAddress[] attributeAddresses =
        profile.getAttributeAddresses(device).toArray(new AttributeAddress[0]);
    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualPowerQuality retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddresses));

    log.info("Retrieving actual power quality");
    final List<GetResult> resultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual power quality", attributeAddresses);

    return this.makeActualPowerQualityResponseDto(resultList, profile.getMetadatas(device));
  }

  private ActualPowerQualityResponseDto makeActualPowerQualityResponseDto(
      final List<GetResult> resultList, final List<PowerQualityObjectMetadata> metadatas)
      throws ProtocolAdapterException {
    final ActualPowerQualityResponseDto responseDto = new ActualPowerQualityResponseDto();
    final ActualPowerQualityDataDto actualPowerQualityDataDto =
        this.makeActualPowerQualityDataDto(resultList, metadatas);
    responseDto.setActualPowerQualityDataDto(actualPowerQualityDataDto);
    return responseDto;
  }

  private ActualPowerQualityDataDto makeActualPowerQualityDataDto(
      final List<GetResult> resultList, final List<PowerQualityObjectMetadata> metadatas)
      throws ProtocolAdapterException {

    final List<PowerQualityObjectDto> powerQualityObjects = new ArrayList<>();
    final List<PowerQualityValueDto> powerQualityValues = new ArrayList<>();

    int idx = 0;
    for (final PowerQualityObjectMetadata metadata : metadatas) {
      final PowerQualityObjectDto powerQualityObject;
      final PowerQualityValueDto powerQualityValue;
      if (metadata.getClassId() == CLASS_ID_CLOCK) {

        final GetResult resultTime = resultList.get(idx++);
        final CosemDateTimeDto cosemDateTime =
            this.dlmsHelper.readDateTime(resultTime, "Actual Power Quality - Time");
        powerQualityObject = new PowerQualityObjectDto(metadata.name(), null);
        powerQualityValue = new PowerQualityValueDto(cosemDateTime.asDateTime().toDate());

      } else if (metadata.getClassId() == CLASS_ID_REGISTER) {

        final GetResult resultValue = resultList.get(idx++);
        final GetResult resultScalerUnit = resultList.get(idx++);

        final DlmsMeterValueDto meterValue =
            this.dlmsHelper.getScaledMeterValue(
                resultValue, resultScalerUnit, "Actual Power Quality - " + metadata.getObisCode());

        final BigDecimal value = meterValue != null ? meterValue.getValue() : null;
        final String unit = meterValue != null ? meterValue.getDlmsUnit().getUnit() : null;
        powerQualityValue = new PowerQualityValueDto(value);

        powerQualityObject = new PowerQualityObjectDto(metadata.name(), unit);

      } else if (metadata.getClassId() == CLASS_ID_DATA) {

        final GetResult resultValue = resultList.get(idx++);

        final Integer meterValue =
            this.dlmsHelper.readInteger(
                resultValue, "Actual Power Quality - " + metadata.getObisCode());

        powerQualityValue =
            meterValue != null ? new PowerQualityValueDto(new BigDecimal(meterValue)) : null;

        powerQualityObject = new PowerQualityObjectDto(metadata.name(), null);
      } else {
        throw new ProtocolAdapterException(
            String.format(
                "Unsupported ClassId {} for logical name {}",
                metadata.getClassId(),
                metadata.obisCode));
      }
      powerQualityObjects.add(powerQualityObject);
      powerQualityValues.add(powerQualityValue);
    }

    return new ActualPowerQualityDataDto(powerQualityObjects, powerQualityValues);
  }

  private Profile determineProfile(final String profileType) {

    try {
      return Profile.valueOf(profileType);
    } catch (final IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException(
          "ActualPowerQuality: an unknown profileType was requested: " + profileType);
    }
  }

  protected static List<PowerQualityObjectMetadata> getMetadatasPublic() {
    return Stream.of(PowerQualityObjectMetadata.values())
        .filter(e -> PUBLIC.equals(e.getProfileName()) || e.getClassId() == CLASS_ID_CLOCK)
        .collect(Collectors.toList());
  }

  protected static List<PowerQualityObjectMetadata> getMetadatasPrivate() {
    return Stream.of(PowerQualityObjectMetadata.values())
        .filter(e -> PRIVATE.equals(e.getProfileName()) || e.getClassId() == CLASS_ID_CLOCK)
        .collect(Collectors.toList());
  }

  @Getter
  protected enum PowerQualityObjectMetadata {
    CLOCK("0.0.1.0.0.255", CLASS_ID_CLOCK, null, false),
    // PRIVATE
    INSTANTANEOUS_CURRENT_L1("1.0.31.7.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    INSTANTANEOUS_CURRENT_L2("1.0.51.7.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    INSTANTANEOUS_CURRENT_L3("1.0.71.7.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    INSTANTANEOUS_ACTIVE_POWER_IMPORT("1.0.1.7.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    INSTANTANEOUS_ACTIVE_POWER_EXPORT("1.0.2.7.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1("1.0.21.7.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2("1.0.41.7.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3("1.0.61.7.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1("1.0.22.7.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2("1.0.42.7.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3("1.0.62.7.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_CURRENT_L1("1.0.31.24.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    AVERAGE_CURRENT_L2("1.0.51.24.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_CURRENT_L3("1.0.71.24.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_ACTIVE_POWER_IMPORT_L1("1.0.21.4.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    AVERAGE_ACTIVE_POWER_IMPORT_L2("1.0.41.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_ACTIVE_POWER_IMPORT_L3("1.0.61.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_ACTIVE_POWER_EXPORT_L1("1.0.22.4.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    AVERAGE_ACTIVE_POWER_EXPORT_L2("1.0.42.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_ACTIVE_POWER_EXPORT_L3("1.0.62.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_REACTIVE_POWER_IMPORT_L1("1.0.23.4.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    AVERAGE_REACTIVE_POWER_IMPORT_L2("1.0.43.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_REACTIVE_POWER_IMPORT_L3("1.0.63.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_REACTIVE_POWER_EXPORT_L1("1.0.24.4.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    AVERAGE_REACTIVE_POWER_EXPORT_L2("1.0.44.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    AVERAGE_REACTIVE_POWER_EXPORT_L3("1.0.64.4.0.255", CLASS_ID_REGISTER, PRIVATE, true),
    INSTANTANEOUS_ACTIVE_CURRENT_TOTAL_OVER_ALL_PHASES(
        "1.0.90.7.0.255", CLASS_ID_REGISTER, PRIVATE, false),
    // PUBLIC
    INSTANTANEOUS_VOLTAGE_L1("1.0.32.7.0.255", CLASS_ID_REGISTER, PUBLIC, false),
    INSTANTANEOUS_VOLTAGE_L2("1.0.52.7.0.255", CLASS_ID_REGISTER, PUBLIC, true),
    INSTANTANEOUS_VOLTAGE_L3("1.0.72.7.0.255", CLASS_ID_REGISTER, PUBLIC, true),
    AVERAGE_VOLTAGE_L1("1.0.32.24.0.255", CLASS_ID_REGISTER, PUBLIC, false),
    AVERAGE_VOLTAGE_L2("1.0.52.24.0.255", CLASS_ID_REGISTER, PUBLIC, true),
    AVERAGE_VOLTAGE_L3("1.0.72.24.0.255", CLASS_ID_REGISTER, PUBLIC, true),
    NUMBER_OF_LONG_POWER_FAILURES("0.0.96.7.9.255", CLASS_ID_DATA, PUBLIC, false),
    NUMBER_OF_POWER_FAILURES("0.0.96.7.21.255", CLASS_ID_DATA, PUBLIC, false),
    NUMBER_OF_VOLTAGE_SAGS_FOR_L1("1.0.32.32.0.255", CLASS_ID_DATA, PUBLIC, false),
    NUMBER_OF_VOLTAGE_SAGS_FOR_L2("1.0.52.32.0.255", CLASS_ID_DATA, PUBLIC, true),
    NUMBER_OF_VOLTAGE_SAGS_FOR_L3("1.0.72.32.0.255", CLASS_ID_DATA, PUBLIC, true),
    NUMBER_OF_VOLTAGE_SWELLS_FOR_L1("1.0.32.36.0.255", CLASS_ID_DATA, PUBLIC, false),
    NUMBER_OF_VOLTAGE_SWELLS_FOR_L2("1.0.52.36.0.255", CLASS_ID_DATA, PUBLIC, true),
    NUMBER_OF_VOLTAGE_SWELLS_FOR_L3("1.0.72.36.0.255", CLASS_ID_DATA, PUBLIC, true);

    private final String obisCode;
    private final int classId;
    private final String profileName;
    private final boolean polyphaseOnly;

    PowerQualityObjectMetadata(
        final String obisCode,
        final int classId,
        final String profileName,
        final boolean polyphaseOnly) {
      this.obisCode = obisCode;
      this.classId = classId;
      this.profileName = profileName;
      this.polyphaseOnly = polyphaseOnly;
    }

    public List<AttributeAddress> getAttributeAddresses() {
      final List<AttributeAddress> attributeAddresses = new ArrayList<>();
      if (this.classId == InterfaceClass.CLOCK.id()) {
        attributeAddresses.add(this.getAttributeAddress(ClockAttribute.TIME.attributeId()));
      } else if (this.classId == InterfaceClass.DATA.id()) {
        attributeAddresses.add(this.getAttributeAddress(DataAttribute.VALUE.attributeId()));
      } else if (this.classId == InterfaceClass.REGISTER.id()) {
        attributeAddresses.add(this.getAttributeAddress(RegisterAttribute.VALUE.attributeId()));
        attributeAddresses.add(
            this.getAttributeAddress(RegisterAttribute.SCALER_UNIT.attributeId()));
      } else {
        log.warn("No attribute addresses returned for interface class of {}", this.name());
      }
      return attributeAddresses;
    }

    public AttributeAddress getAttributeAddress(final int attributeId) {
      return new AttributeAddress(this.classId, this.obisCode, attributeId);
    }
  }

  private enum Profile {
    PUBLIC(getMetadatasPublic()),
    PRIVATE(getMetadatasPrivate());

    private final List<PowerQualityObjectMetadata> metadatas;

    Profile(final List<PowerQualityObjectMetadata> metadatas) {
      this.metadatas = metadatas;
    }

    public List<PowerQualityObjectMetadata> getMetadatas(final DlmsDevice device) {
      return this.metadatas.stream()
          .filter(metadata -> this.useForDevice(metadata, device))
          .collect(Collectors.toList());
    }

    public List<AttributeAddress> getAttributeAddresses(final DlmsDevice device) {

      return this.getMetadatas(device).stream()
          .flatMap(metadata -> metadata.getAttributeAddresses().stream())
          .collect(Collectors.toList());
    }

    private boolean useForDevice(
        final PowerQualityObjectMetadata metadata, final DlmsDevice device) {
      if (device.isPolyphase()) {
        return true;
      }
      return !metadata.polyphaseOnly;
    }
  }
}
