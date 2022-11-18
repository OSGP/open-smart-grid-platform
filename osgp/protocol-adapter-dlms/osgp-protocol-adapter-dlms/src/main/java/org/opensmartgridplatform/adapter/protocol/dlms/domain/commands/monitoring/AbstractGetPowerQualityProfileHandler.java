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
import java.util.*;
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
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.*;
import org.opensmartgridplatform.dlms.objectconfig.*;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.*;
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

  private enum Profile {
    DEFINABLE_LOAD_PROFILE(OBIS_CODE_DEFINABLE_LOAD_PROFILE, INTERVAL_DEFINABLE_LOAD_PROFILE),
    PROFILE_1_PRIVATE(OBIS_CODE_PROFILE_1, INTERVAL_PROFILE_1),
    PROFILE_2_PUBLIC(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2),
    PROFILE_2_PRIVATE(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2);

    private final ObisCodeValuesDto obisCodeValuesDto;
    private final int interval;

    Profile(final ObisCodeValuesDto obisCodeValuesDto, final int interval) {
      this.obisCodeValuesDto = obisCodeValuesDto;
      this.interval = interval;
    }

    public ObisCodeValuesDto getObisCodeValuesDto() {
      return this.obisCodeValuesDto;
    }

    public int getInterval() {
      return this.interval;
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
  private ObjectConfigService objectConfigService;

  protected AbstractGetPowerQualityProfileHandler(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    this.dlmsHelper = dlmsHelper;
    this.objectConfigService = objectConfigService;
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

    List<CosemObject> cosemConfigObjects = this.getCosemObjects(device, profileType);

    for (final Profile profile : profiles) {

      final ObisCode obisCode = this.getObisCodeFromProfile(profile.name());
      final DateTime beginDateTime =
          toDateTime(getPowerQualityProfileRequestDataDto.getBeginDate(), device.getTimezone());
      final DateTime endDateTime =
          toDateTime(getPowerQualityProfileRequestDataDto.getEndDate(), device.getTimezone());

      // all value types that can be selected within this profile.
      final List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);

      // the values that are allowed to be retrieved from the meter, used
      // as filter either before (SMR 5.1+) or
      // after data retrieval

      final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects =
          this.createSelectableCaptureObjects(captureObjects, cosemConfigObjects);

      // the units of measure for all Selectable Capture objects
      final List<ScalerUnitInfo> scalerUnitInfos =
          this.createScalerUnitInfos(selectableCaptureObjects.values(), cosemConfigObjects);

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

  private List<CosemObject> getCosemObjects(final DlmsDevice device, final String profileType)
      throws ProtocolAdapterException {
    List<CosemObject> cosemConfigObjects = new ArrayList<>();

    try {
      final CosemObject clockObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(), device.getProtocolVersion(), DlmsObjectType.CLOCK);
      cosemConfigObjects.add(clockObject);

      final EnumMap<ObjectProperty, List<Object>> pqProperties =
          new EnumMap<>(ObjectProperty.class);
      pqProperties.put(ObjectProperty.PQ_PROFILE, Collections.singletonList(profileType));
      pqProperties.put(
          ObjectProperty.PQ_REQUEST,
          Arrays.asList(PowerQualityRequest.ONDEMAND.name(), PowerQualityRequest.BOTH.name()));

      List<CosemObject> cosemObjectsWithProperties =
          this.objectConfigService.getCosemObjectsWithProperties(
              device.getProtocolName(), device.getProtocolVersion(), pqProperties);

      cosemConfigObjects.addAll(cosemObjectsWithProperties);

      return cosemConfigObjects;
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Error in object config", e);
    }
  }

  private List<Profile> determineProfileForDevice(final String profileType) {

    switch (profileType) {
      case PUBLIC:
        return Arrays.asList(Profile.DEFINABLE_LOAD_PROFILE, Profile.PROFILE_2_PUBLIC);
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

  private ObisCode getObisCodeFromProfile(String profileName) {
    Optional<CosemObject> profileFromConfig = this.objectConfigService.getProfile(profileName);
    if (profileFromConfig.isPresent()) {
      return new ObisCode(profileFromConfig.get().getObis());
    }
    return null;
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
      final List<GetResult> captureObjects, final List<CosemObject> cosemConfigObjects)
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

        Optional<String> matchedObisCode =
            cosemConfigObjects.stream()
                .map(CosemObject::getObis)
                .filter(cosemObjectDefinitionDto.getLogicalName().toString()::equals)
                .findFirst();

        if (matchedObisCode.isPresent()) {
          selectableCaptureObjects.put(
              positionInDataObjectsList,
              new CaptureObjectDefinitionDto(
                  cosemObjectDefinitionDto.getClassId(),
                  new ObisCodeValuesDto(matchedObisCode.get()),
                  (byte) cosemObjectDefinitionDto.getAttributeIndex(),
                  cosemObjectDefinitionDto.getDataIndex()));
        }
      }
    }

    return selectableCaptureObjects;
  }

  private List<ScalerUnitInfo> createScalerUnitInfos(
      final Collection<CaptureObjectDefinitionDto> values,
      final List<CosemObject> cosemConfigObjects) {

    final List<ScalerUnitInfo> scalerUnitInfos = new ArrayList<>();

    for (final CaptureObjectDefinitionDto dto : values) {

      final ScalerUnitInfo newScalerUnitInfo = this.createScalerUnitInfo(dto, cosemConfigObjects);
      scalerUnitInfos.add(newScalerUnitInfo);
    }

    return scalerUnitInfos;
  }

  private ScalerUnitInfo createScalerUnitInfo(
      final CaptureObjectDefinitionDto captureObjectDefinitionDto,
      final List<CosemObject> cosemConfigObjects) {

    final int classId = captureObjectDefinitionDto.getClassId();
    final String logicalName = captureObjectDefinitionDto.getLogicalName().toString();

    Optional<List<Attribute>> attributeList =
        cosemConfigObjects.stream()
            .filter(object -> object.getObis().equals(logicalName))
            .map(CosemObject::getAttributes)
            .findFirst();
    Optional<Attribute> scalUnitType =
        attributeList.get().stream()
            .filter(attribute -> attribute.getDatatype().toString().equals("scal_unit_type"))
            .findFirst();

    if (scalUnitType.isPresent()) {
      final DataObject scalerUnitDataObject =
          DataObject.newStructureData((List<DataObject>) scalUnitType.get());
      return new ScalerUnitInfo(logicalName, classId, scalerUnitDataObject);
    } else {
      return new ScalerUnitInfo(logicalName, classId, null);
    }
  }
}
