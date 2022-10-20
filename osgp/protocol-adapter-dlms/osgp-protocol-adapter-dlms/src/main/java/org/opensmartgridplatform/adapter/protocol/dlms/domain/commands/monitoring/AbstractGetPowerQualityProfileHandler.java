/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter.toDateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ScalerUnitInfo;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DemandRegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGetPowerQualityProfileHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractGetPowerQualityProfileHandler.class);

  private static final String CAPTURE_OBJECT = "capture-object";

  private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

  private static final ObisCodeValuesDto OBIS_CODE_DEFINABLE_LOAD_PROFILE =
      new ObisCodeValuesDto((byte) 0, (byte) 1, (byte) 94, (byte) 31, (byte) 6, (byte) 255);
  private static final ObisCodeValuesDto OBIS_CODE_PROFILE_1 =
      new ObisCodeValuesDto((byte) 1, (byte) 0, (byte) 99, (byte) 1, (byte) 1, (byte) 255);
  private static final ObisCodeValuesDto OBIS_CODE_PROFILE_2 =
      new ObisCodeValuesDto((byte) 1, (byte) 0, (byte) 99, (byte) 1, (byte) 2, (byte) 255);

  private static final int INTERVAL_DEFINABLE_LOAD_PROFILE = 15;
  private static final int INTERVAL_PROFILE_1 = 15;
  private static final int INTERVAL_PROFILE_2 = 10;
  private static final String PUBLIC = "PUBLIC";
  private static final String PRIVATE = "PRIVATE";

  private enum SelectableObisCode {
    OBIS_CODE_CLOCK("0.0.1.0.0.255"),
    NR_VOLTAGE_SAGS_L1("1.0.32.32.0.255"),
    NR_VOLTAGE_SAGS_L2("1.0.52.32.0.255"),
    NR_VOLTAGE_SAGS_L3("1.0.72.32.0.255"),
    NR_VOLTAGE_SWELLS_L1("1.0.32.36.0.255"),
    NR_VOLTAGE_SWELLS_L2("1.0.52.36.0.255"),
    NR_VOLTAGE_SWELLS_L3("1.0.72.36.0.255"),
    NR_POWER_FAILURES("0.0.96.7.21.255"),
    AVERAGE_VOLTAGE_L1("1.0.32.24.0.255"),
    AVERAGE_VOLTAGE_L2("1.0.52.24.0.255"),
    AVERAGE_VOLTAGE_L3("1.0.72.24.0.255"),
    INSTANTANEOUS_VOLTAGE_L1("1.0.32.7.0.255"),
    CDMA_DIAGNOSTICS("0.1.25.6.0.255"),
    GPRS_DIAGNOSTICS("0.0.25.6.0.255"),
    MBUS_CLIENT_SETUP_CHANNEL1("0.1.24.1.0.255"),
    MBUS_CLIENT_SETUP_CHANNEL2("0.2.24.1.0.255"),
    MBUS_DIAGNOSTICS_CHANNEL1("0.1.24.9.0.255"),
    MBUS_DIAGNOSTICS_CHANNEL2("0.2.24.9.0.255"),
    AVERAGE_ACTIVE_POWER_IMPORT_L1("1.0.21.4.0.255"),
    AVERAGE_ACTIVE_POWER_IMPORT_L2("1.0.41.4.0.255"),
    AVERAGE_ACTIVE_POWER_IMPORT_L3("1.0.61.4.0.255"),
    AVERAGE_ACTIVE_POWER_EXPORT_L1("1.0.22.4.0.255"),
    AVERAGE_ACTIVE_POWER_EXPORT_L2("1.0.42.4.0.255"),
    AVERAGE_ACTIVE_POWER_EXPORT_L3("1.0.62.4.0.255"),
    AVERAGE_REACTIVE_POWER_IMPORT_L1("1.0.23.4.0.255"),
    AVERAGE_REACTIVE_POWER_IMPORT_L2("1.0.43.4.0.255"),
    AVERAGE_REACTIVE_POWER_IMPORT_L3("1.0.63.4.0.255"),
    AVERAGE_REACTIVE_POWER_EXPORT_L1("1.0.24.4.0.255"),
    AVERAGE_REACTIVE_POWER_EXPORT_L2("1.0.44.4.0.255"),
    AVERAGE_REACTIVE_POWER_EXPORT_L3("1.0.64.4.0.255"),
    AVERAGE_CURRENT_L1("1.0.31.24.0.255"),
    AVERAGE_CURRENT_L2("1.0.51.24.0.255"),
    AVERAGE_CURRENT_L3("1.0.71.24.0.255"),
    INSTANTANEOUS_CURRENT_L1("1.0.31.7.0.255");

    private final String obisCode;

    SelectableObisCode(final String obisCode) {
      this.obisCode = obisCode;
    }

    public String getObisCode() {
      return this.obisCode;
    }

    static Optional<SelectableObisCode> getByObisCode(final String obisCode) {
      return Arrays.stream(SelectableObisCode.values())
          .filter(value -> value.getObisCode().equals(obisCode))
          .findFirst();
    }
  }

  private enum Profile {
    DEFINABLE_LOAD_PROFILE_PUBLIC(
        OBIS_CODE_DEFINABLE_LOAD_PROFILE,
        INTERVAL_DEFINABLE_LOAD_PROFILE,
        getLogicalNamesPublicDefinableLoadProfile()),
    PROFILE_1_PRIVATE(OBIS_CODE_PROFILE_1, INTERVAL_PROFILE_1, getLogicalNamesPrivateProfile1()),
    PROFILE_2_PUBLIC(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2, getLogicalNamesPublicProfile2()),
    PROFILE_2_PRIVATE(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2, getLogicalNamesPrivateProfile2());

    private final ObisCodeValuesDto obisCodeValuesDto;
    private final int interval;
    private final List<SelectableObisCode> logicalNames;

    Profile(
        final ObisCodeValuesDto obisCodeValuesDto,
        final int interval,
        final List<SelectableObisCode> logicalNames) {
      this.obisCodeValuesDto = obisCodeValuesDto;
      this.interval = interval;
      this.logicalNames = logicalNames;
    }

    public ObisCodeValuesDto getObisCodeValuesDto() {
      return this.obisCodeValuesDto;
    }

    public int getInterval() {
      return this.interval;
    }

    public List<SelectableObisCode> getLogicalNames() {
      return this.logicalNames;
    }
  }

  private static final byte[] OBIS_BYTES_CLOCK = new byte[] {0, 0, 1, 0, 0, (byte) 255};

  private static final Map<Integer, Integer> SCALER_UNITS_MAP = new HashMap<>();

  static {
    SCALER_UNITS_MAP.put(InterfaceClass.REGISTER.id(), RegisterAttribute.SCALER_UNIT.attributeId());
    SCALER_UNITS_MAP.put(
        InterfaceClass.EXTENDED_REGISTER.id(), ExtendedRegisterAttribute.SCALER_UNIT.attributeId());
    SCALER_UNITS_MAP.put(
        InterfaceClass.DEMAND_REGISTER.id(), DemandRegisterAttribute.SCALER_UNIT.attributeId());
  }

  protected final DlmsHelper dlmsHelper;

  protected AbstractGetPowerQualityProfileHandler(final DlmsHelper dlmsHelper) {
    this.dlmsHelper = dlmsHelper;
  }

  protected abstract DataObject convertSelectableCaptureObjects(
      final List<CaptureObjectDefinitionDto> selectableCaptureObjects);

  protected abstract List<ProfileEntryValueDto> createProfileEntryValueDto(
      final DataObject profileEntryDataObject,
      final List<ScalerUnitInfo> scalerUnitInfos,
      ProfileEntryDto previousProfileEntryDto,
      final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects,
      int timeInterval);

  protected GetPowerQualityProfileResponseDto handle(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto)
      throws ProtocolAdapterException {

    final String profileType = getPowerQualityProfileRequestDataDto.getProfileType();
    final List<Profile> profiles = this.determineProfileForDevice(profileType);
    final GetPowerQualityProfileResponseDto response = new GetPowerQualityProfileResponseDto();
    final List<PowerQualityProfileDataDto> responseDatas = new ArrayList<>();

    for (final Profile profile : profiles) {

      final ObisCode obisCode = this.makeObisCode(profile.getObisCodeValuesDto());
      final DateTime beginDateTime =
          toDateTime(getPowerQualityProfileRequestDataDto.getBeginDate(), device);
      final DateTime endDateTime =
          toDateTime(getPowerQualityProfileRequestDataDto.getEndDate(), device);

      // all value types that can be selected within this profile.
      final List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);

      // the values that are allowed to be retrieved from the meter, used
      // as filter either before (SMR 5.1+) or
      // after data retrieval
      final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects =
          this.createSelectableCaptureObjects(captureObjects, profile.getLogicalNames());

      // the units of measure for all Selectable Capture objects
      final List<ScalerUnitInfo> scalerUnitInfos =
          this.createScalerUnitInfos(conn, device, selectableCaptureObjects.values());

      final List<GetResult> bufferList =
          this.retrieveBuffer(
              conn,
              device,
              obisCode,
              beginDateTime,
              endDateTime,
              new ArrayList<>(selectableCaptureObjects.values()));

      final PowerQualityProfileDataDto responseDataDto =
          this.processData(
              profile, captureObjects, scalerUnitInfos, selectableCaptureObjects, bufferList);

      responseDatas.add(responseDataDto);
    }

    response.setPowerQualityProfileDatas(responseDatas);

    return response;
  }

  private List<Profile> determineProfileForDevice(final String profileType) {

    switch (profileType) {
      case PUBLIC:
        return Arrays.asList(Profile.DEFINABLE_LOAD_PROFILE_PUBLIC, Profile.PROFILE_2_PUBLIC);
      case PRIVATE:
        return Arrays.asList(Profile.PROFILE_1_PRIVATE, Profile.PROFILE_2_PRIVATE);
      default:
        throw new IllegalArgumentException(
            "GetPowerQualityProfile: an unknown profileType was requested: " + profileType);
    }
  }

  private List<GetResult> retrieveCaptureObjects(
      final DlmsConnectionManager conn, final DlmsDevice device, final ObisCode obisCode)
      throws ProtocolAdapterException {
    final AttributeAddress captureObjectsAttributeAddress =
        new AttributeAddress(
            InterfaceClass.PROFILE_GENERIC.id(),
            obisCode,
            ProfileGenericAttribute.CAPTURE_OBJECTS.attributeId());

    return this.dlmsHelper.getAndCheck(
        conn, device, "retrieve profile generic capture objects", captureObjectsAttributeAddress);
  }

  private List<GetResult> retrieveBuffer(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ObisCode obisCode,
      final DateTime beginDateTime,
      final DateTime endDateTime,
      final List<CaptureObjectDefinitionDto> selectableCaptureObjects)
      throws ProtocolAdapterException {

    final DataObject selectableValues =
        this.convertSelectableCaptureObjects(selectableCaptureObjects);

    final SelectiveAccessDescription selectiveAccessDescription =
        this.getSelectiveAccessDescription(beginDateTime, endDateTime, selectableValues);
    final AttributeAddress bufferAttributeAddress =
        new AttributeAddress(
            InterfaceClass.PROFILE_GENERIC.id(),
            obisCode,
            ProfileGenericAttribute.BUFFER.attributeId(),
            selectiveAccessDescription);

    return this.dlmsHelper.getAndCheck(
        conn, device, "retrieve profile generic buffer", bufferAttributeAddress);
  }

  private PowerQualityProfileDataDto processData(
      final Profile profile,
      final List<GetResult> captureObjects,
      final List<ScalerUnitInfo> scalerUnitInfos,
      final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects,
      final List<GetResult> bufferList)
      throws ProtocolAdapterException {

    final List<CaptureObjectDto> captureObjectDtos =
        this.createSelectableCaptureObjects(
            captureObjects, scalerUnitInfos, new ArrayList<>(selectableCaptureObjects.values()));
    final List<ProfileEntryDto> profileEntryDtos =
        this.createProfileEntries(
            bufferList, scalerUnitInfos, selectableCaptureObjects, profile.getInterval());
    return new PowerQualityProfileDataDto(
        profile.getObisCodeValuesDto(), captureObjectDtos, profileEntryDtos);
  }

  private List<ProfileEntryDto> createProfileEntries(
      final List<GetResult> bufferList,
      final List<ScalerUnitInfo> scalerUnitInfos,
      final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects,
      final int timeInterval) {

    final List<ProfileEntryDto> profileEntryDtos = new ArrayList<>();

    // there is always only one GetResult, which is an array of array data
    for (final GetResult buffer : bufferList) {
      final DataObject dataObject = buffer.getResultData();

      final List<DataObject> dataObjectValue = dataObject.getValue();
      ProfileEntryDto previousProfileEntryDto = null;

      for (final DataObject profileEntryDataObject : dataObjectValue) {

        final ProfileEntryDto profileEntryDto =
            new ProfileEntryDto(
                this.createProfileEntryValueDto(
                    profileEntryDataObject,
                    scalerUnitInfos,
                    previousProfileEntryDto,
                    selectableCaptureObjects,
                    timeInterval));

        profileEntryDtos.add(profileEntryDto);

        previousProfileEntryDto = profileEntryDto;
      }
    }
    return profileEntryDtos;
  }

  // the available CaptureObjects are filtered with the ones that can be
  // selected
  private List<CaptureObjectDto> createSelectableCaptureObjects(
      final List<GetResult> captureObjects,
      final List<ScalerUnitInfo> scalerUnitInfos,
      final List<CaptureObjectDefinitionDto> selectableCaptureObjects)
      throws ProtocolAdapterException {

    final List<CaptureObjectDto> captureObjectDtos = new ArrayList<>();
    for (final GetResult captureObjectResult : captureObjects) {
      final DataObject dataObject = captureObjectResult.getResultData();
      final List<DataObject> captureObjectList = dataObject.getValue();
      for (final DataObject object : captureObjectList) {
        final boolean addCaptureObject = this.isSelectableValue(selectableCaptureObjects, object);
        if (addCaptureObject) {
          captureObjectDtos.add(
              this.makeCaptureObjectDto(object, scalerUnitInfos.get(captureObjectDtos.size())));
        }
      }
    }
    return captureObjectDtos;
  }

  private boolean isSelectableValue(
      final List<CaptureObjectDefinitionDto> selectedValues, final DataObject dataObject)
      throws ProtocolAdapterException {

    final CosemObjectDefinitionDto cosemObjectDefinitionDto =
        this.dlmsHelper.readObjectDefinition(dataObject, CAPTURE_OBJECT);

    if (this.isClockDefinition(cosemObjectDefinitionDto)) {
      // The captured clock is always included.
      return true;
    }

    return selectedValues.stream()
        .anyMatch(
            selectedValue ->
                this.isDefinitionOfSameObject(cosemObjectDefinitionDto, selectedValue));
  }

  private boolean isClockDefinition(final CosemObjectDefinitionDto cosemObjectDefinitionDto) {

    final int classId = cosemObjectDefinitionDto.getClassId();
    final byte[] obisBytes = cosemObjectDefinitionDto.getLogicalName().toByteArray();
    final byte attributeIndex = (byte) cosemObjectDefinitionDto.getAttributeIndex();
    final int dataIndex = cosemObjectDefinitionDto.getDataIndex();

    return InterfaceClass.CLOCK.id() == classId
        && Arrays.equals(OBIS_BYTES_CLOCK, obisBytes)
        && ClockAttribute.TIME.attributeId() == attributeIndex
        && 0 == dataIndex;
  }

  private boolean isDefinitionOfSameObject(
      final CosemObjectDefinitionDto cosemObjectDefinitionDto,
      final CaptureObjectDefinitionDto captureObjectDefinition) {

    final int classIdCosemObjectDefinition = cosemObjectDefinitionDto.getClassId();
    final byte[] obisBytesCosemObjectDefinition =
        cosemObjectDefinitionDto.getLogicalName().toByteArray();
    final byte attributeIndexCosemObjectDefinition =
        (byte) cosemObjectDefinitionDto.getAttributeIndex();
    final int dataIndexCosemObjectDefinition = cosemObjectDefinitionDto.getDataIndex();
    final int classIdCaptureObjectDefinition = captureObjectDefinition.getClassId();
    final byte[] obisBytesCaptureObjectDefinition =
        captureObjectDefinition.getLogicalName().toByteArray();
    final byte attributeIndexCaptureObjectDefinition = captureObjectDefinition.getAttributeIndex();
    final int dataIndexCaptureObjectDefinition =
        captureObjectDefinition.getDataIndex() == null ? 0 : captureObjectDefinition.getDataIndex();

    return classIdCaptureObjectDefinition == classIdCosemObjectDefinition
        && Arrays.equals(obisBytesCaptureObjectDefinition, obisBytesCosemObjectDefinition)
        && attributeIndexCaptureObjectDefinition == attributeIndexCosemObjectDefinition
        && dataIndexCaptureObjectDefinition == dataIndexCosemObjectDefinition;
  }

  private SelectiveAccessDescription getSelectiveAccessDescription(
      final DateTime beginDateTime,
      final DateTime endDateTime,
      final DataObject selectableCaptureObjects) {

    /*
     * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
     * restricting object in a range descriptor with a from value and to
     * value to determine which elements from the buffered array should be
     * retrieved.
     */
    final DataObject clockDefinition = this.dlmsHelper.getClockDefinition();
    final DataObject fromValue = this.dlmsHelper.asDataObject(beginDateTime);
    final DataObject toValue = this.dlmsHelper.asDataObject(endDateTime);

    final DataObject accessParameter =
        DataObject.newStructureData(
            Arrays.asList(clockDefinition, fromValue, toValue, selectableCaptureObjects));

    return new SelectiveAccessDescription(ACCESS_SELECTOR_RANGE_DESCRIPTOR, accessParameter);
  }

  private ObisCode makeObisCode(final ObisCodeValuesDto obisCodeValues) {
    return new ObisCode(obisCodeValues.toByteArray());
  }

  private CaptureObjectDto makeCaptureObjectDto(
      final DataObject captureObjectDataObject, final ScalerUnitInfo scalerUnitInfo)
      throws ProtocolAdapterException {

    final CosemObjectDefinitionDto cosemObjectDefinitionDto =
        this.dlmsHelper.readObjectDefinition(captureObjectDataObject, CAPTURE_OBJECT);

    return new CaptureObjectDto(
        cosemObjectDefinitionDto.getClassId(),
        cosemObjectDefinitionDto.getLogicalName().toString(),
        cosemObjectDefinitionDto.getAttributeIndex(),
        cosemObjectDefinitionDto.getDataIndex(),
        this.getUnit(scalerUnitInfo));
  }

  private DlmsUnitTypeDto getUnitType(final ScalerUnitInfo scalerUnitInfo) {
    if (scalerUnitInfo.getScalerUnit() != null) {
      final List<DataObject> dataObjects = scalerUnitInfo.getScalerUnit().getValue();
      final int index = Integer.parseInt(dataObjects.get(1).getValue().toString());
      final DlmsUnitTypeDto unitType = DlmsUnitTypeDto.getUnitType(index);
      if (unitType != null) {
        return unitType;
      }
    }
    return DlmsUnitTypeDto.UNDEFINED;
  }

  private String getUnit(final ScalerUnitInfo scalerUnitInfo) {
    return this.getUnitType(scalerUnitInfo).getUnit();
  }

  protected ProfileEntryValueDto makeProfileEntryValueDto(
      final DataObject dataObject,
      final ScalerUnitInfo scalerUnitInfo,
      final ProfileEntryDto previousProfileEntryDto,
      final int timeInterval) {
    if (InterfaceClass.CLOCK.id() == scalerUnitInfo.getClassId()) {
      return this.makeDateProfileEntryValueDto(dataObject, previousProfileEntryDto, timeInterval);
    } else if (dataObject.isNumber()) {
      return this.createNumericProfileEntryValueDto(dataObject, scalerUnitInfo);
    } else if (dataObject.isNull()) {
      return new ProfileEntryValueDto(null);
    } else {
      final String dbgInfo = this.dlmsHelper.getDebugInfo(dataObject);
      LOGGER.debug("creating ProfileEntryDto from {} {} ", dbgInfo, scalerUnitInfo);
      return new ProfileEntryValueDto(dbgInfo);
    }
  }

  private ProfileEntryValueDto makeDateProfileEntryValueDto(
      final DataObject dataObject,
      final ProfileEntryDto previousProfileEntryDto,
      final int timeInterval) {

    final CosemDateTimeDto cosemDateTime = this.dlmsHelper.convertDataObjectToDateTime(dataObject);

    if (cosemDateTime == null) {
      // in case of null date, we calculate the date based on the always
      // existing previous value plus interval
      final Date previousDate =
          (Date) previousProfileEntryDto.getProfileEntryValues().get(0).getValue();
      final LocalDateTime newLocalDateTime =
          Instant.ofEpochMilli(previousDate.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime()
              .plusMinutes(timeInterval);

      return new ProfileEntryValueDto(
          Date.from(newLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()));
    } else {
      return new ProfileEntryValueDto(cosemDateTime.asDateTime().toDate());
    }
  }

  private ProfileEntryValueDto createNumericProfileEntryValueDto(
      final DataObject dataObject, final ScalerUnitInfo scalerUnitInfo) {
    try {
      if (scalerUnitInfo.getScalerUnit() != null) {
        final DlmsMeterValueDto meterValue =
            this.dlmsHelper.getScaledMeterValue(
                dataObject, scalerUnitInfo.getScalerUnit(), "getScaledMeterValue");
        if (DlmsUnitTypeDto.COUNT.equals(this.getUnitType(scalerUnitInfo))) {
          return new ProfileEntryValueDto(meterValue.getValue().longValue());
        } else {
          return new ProfileEntryValueDto(meterValue.getValue());
        }
      } else {
        final long value = this.dlmsHelper.readLong(dataObject, "read long");
        return new ProfileEntryValueDto(value);
      }
    } catch (final ProtocolAdapterException e) {
      LOGGER.error("Error creating ProfileEntryDto from {}", dataObject, e);
      final String debugInfo = this.dlmsHelper.getDebugInfo(dataObject);
      return new ProfileEntryValueDto(debugInfo);
    }
  }

  private Map<Integer, CaptureObjectDefinitionDto> createSelectableCaptureObjects(
      final List<GetResult> captureObjects, final List<SelectableObisCode> logicalNames)
      throws ProtocolAdapterException {

    final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects = new HashMap<>();

    // there is always only one GetResult
    for (final GetResult captureObjectResult : captureObjects) {

      final List<DataObject> dataObjects = captureObjectResult.getResultData().getValue();

      for (int positionInDataObjectsList = 0;
          positionInDataObjectsList < dataObjects.size();
          positionInDataObjectsList++) {

        final DataObject dataObject = dataObjects.get(positionInDataObjectsList);

        final CosemObjectDefinitionDto cosemObjectDefinitionDto =
            this.dlmsHelper.readObjectDefinition(dataObject, CAPTURE_OBJECT);

        final Optional<SelectableObisCode> logicalName =
            SelectableObisCode.getByObisCode(cosemObjectDefinitionDto.getLogicalName().toString());

        if (logicalName.isPresent() && logicalNames.contains(logicalName.get())) {
          selectableCaptureObjects.put(
              positionInDataObjectsList,
              new CaptureObjectDefinitionDto(
                  cosemObjectDefinitionDto.getClassId(),
                  new ObisCodeValuesDto(logicalName.get().obisCode),
                  (byte) cosemObjectDefinitionDto.getAttributeIndex(),
                  cosemObjectDefinitionDto.getDataIndex()));
        }
      }
    }

    return selectableCaptureObjects;
  }

  private List<ScalerUnitInfo> createScalerUnitInfos(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Collection<CaptureObjectDefinitionDto> values)
      throws ProtocolAdapterException {

    final List<ScalerUnitInfo> scalerUnitInfos = new ArrayList<>();

    for (final CaptureObjectDefinitionDto dto : values) {

      final ScalerUnitInfo newScalerUnitInfo = this.createScalerUnitInfo(conn, device, dto);
      scalerUnitInfos.add(newScalerUnitInfo);
    }

    return scalerUnitInfos;
  }

  private ScalerUnitInfo createScalerUnitInfo(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final CaptureObjectDefinitionDto captureObjectDefinitionDto)
      throws ProtocolAdapterException {

    final int classId = captureObjectDefinitionDto.getClassId();
    final String logicalName = captureObjectDefinitionDto.getLogicalName().toString();

    if (this.hasScalerUnit(classId)) {
      final AttributeAddress addr =
          new AttributeAddress(classId, logicalName, SCALER_UNITS_MAP.get(classId));
      final List<GetResult> scalerUnitResult =
          this.dlmsHelper.getAndCheck(
              conn, device, "retrieve scaler unit for capture object", addr);
      final DataObject scalerUnitDataObject = scalerUnitResult.get(0).getResultData();
      return new ScalerUnitInfo(logicalName, classId, scalerUnitDataObject);
    } else {
      return new ScalerUnitInfo(logicalName, classId, null);
    }
  }

  private static List<SelectableObisCode> getLogicalNamesPublicDefinableLoadProfile() {

    return Arrays.asList(
        SelectableObisCode.OBIS_CODE_CLOCK,
        SelectableObisCode.NR_VOLTAGE_SAGS_L1,
        SelectableObisCode.NR_VOLTAGE_SAGS_L2,
        SelectableObisCode.NR_VOLTAGE_SAGS_L3,
        SelectableObisCode.NR_VOLTAGE_SWELLS_L1,
        SelectableObisCode.NR_VOLTAGE_SWELLS_L2,
        SelectableObisCode.NR_VOLTAGE_SWELLS_L3,
        SelectableObisCode.NR_POWER_FAILURES,
        SelectableObisCode.CDMA_DIAGNOSTICS,
        SelectableObisCode.GPRS_DIAGNOSTICS,
        SelectableObisCode.MBUS_CLIENT_SETUP_CHANNEL1,
        SelectableObisCode.MBUS_CLIENT_SETUP_CHANNEL2,
        SelectableObisCode.MBUS_DIAGNOSTICS_CHANNEL1,
        SelectableObisCode.MBUS_DIAGNOSTICS_CHANNEL2);
  }

  private static List<SelectableObisCode> getLogicalNamesPublicProfile2() {
    return Arrays.asList(
        SelectableObisCode.OBIS_CODE_CLOCK,
        SelectableObisCode.AVERAGE_VOLTAGE_L1,
        SelectableObisCode.AVERAGE_VOLTAGE_L2,
        SelectableObisCode.AVERAGE_VOLTAGE_L3,
        SelectableObisCode.INSTANTANEOUS_VOLTAGE_L1);
  }

  private static List<SelectableObisCode> getLogicalNamesPrivateProfile1() {
    return Arrays.asList(
        SelectableObisCode.OBIS_CODE_CLOCK,
        SelectableObisCode.AVERAGE_ACTIVE_POWER_IMPORT_L1,
        SelectableObisCode.AVERAGE_ACTIVE_POWER_IMPORT_L2,
        SelectableObisCode.AVERAGE_ACTIVE_POWER_IMPORT_L3,
        SelectableObisCode.AVERAGE_ACTIVE_POWER_EXPORT_L1,
        SelectableObisCode.AVERAGE_ACTIVE_POWER_EXPORT_L2,
        SelectableObisCode.AVERAGE_ACTIVE_POWER_EXPORT_L3,
        SelectableObisCode.AVERAGE_REACTIVE_POWER_IMPORT_L1,
        SelectableObisCode.AVERAGE_REACTIVE_POWER_IMPORT_L2,
        SelectableObisCode.AVERAGE_REACTIVE_POWER_IMPORT_L3,
        SelectableObisCode.AVERAGE_REACTIVE_POWER_EXPORT_L1,
        SelectableObisCode.AVERAGE_REACTIVE_POWER_EXPORT_L2,
        SelectableObisCode.AVERAGE_REACTIVE_POWER_EXPORT_L3);
  }

  private static List<SelectableObisCode> getLogicalNamesPrivateProfile2() {
    return Arrays.asList(
        SelectableObisCode.OBIS_CODE_CLOCK,
        SelectableObisCode.AVERAGE_CURRENT_L1,
        SelectableObisCode.AVERAGE_CURRENT_L2,
        SelectableObisCode.AVERAGE_CURRENT_L3,
        SelectableObisCode.INSTANTANEOUS_CURRENT_L1);
  }

  private boolean hasScalerUnit(final int classId) {
    return SCALER_UNITS_MAP.containsKey(classId);
  }
}
