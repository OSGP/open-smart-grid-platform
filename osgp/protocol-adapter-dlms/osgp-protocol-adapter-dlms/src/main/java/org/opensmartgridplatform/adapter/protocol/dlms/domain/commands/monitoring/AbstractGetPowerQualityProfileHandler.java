// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter.toDateTime;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DEFINABLE_LOAD_PROFILE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_QUALITY_PROFILE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_QUALITY_PROFILE_2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGetPowerQualityProfileHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractGetPowerQualityProfileHandler.class);

  private static final String CAPTURE_OBJECT = "capture-object";

  private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

  private static final int ATTRIBUTE_ID_INTERVAL = 4;

  private static final int DATA_INDEX_SIGNAL_QUALITY = 3;
  private static final int DATA_INDEX_BER = 4;

  private static final List<Integer> CHANNELS = Arrays.asList(1, 2, 3, 4);

  protected final DlmsHelper dlmsHelper;
  private final ObjectConfigService objectConfigService;

  protected AbstractGetPowerQualityProfileHandler(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    this.dlmsHelper = dlmsHelper;
    this.objectConfigService = objectConfigService;
  }

  protected abstract DataObject convertSelectableCaptureObjects(
      final List<SelectableObject> selectableCaptureObjects);

  protected abstract List<ProfileEntryValueDto> createProfileEntryValueDto(
      final DataObject profileEntryDataObject,
      ProfileEntryDto previousProfileEntryDto,
      final LinkedHashMap<Integer, SelectableObject> selectableCaptureObjects,
      int timeInterval);

  protected GetPowerQualityProfileResponseDto handle(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetPowerQualityProfileRequestDataDto request)
      throws ProtocolAdapterException {

    // Power quality values can be private or public and can be read from the buffers in several
    // profiles (DLMS class-id 7) in the meter. The values available in meters vary, due to the
    // protocol version, the meter type or a different setting written by the head end system.
    // Some meters also don't support selective access. In that case we read more values than
    // requested from the meter and need to filter afterwards to get the wanted values.
    // The steps to take are as follows:
    // 1. Determine which profiles should be read and the values they can contain (config objects)
    // 2. Get the available values in the meter (capture objects)
    // 3. Only objects that are in both lists (config and capture objects) can be read: the
    //    selectable objects
    // 4. Retrieve the values from the meter, if possible only get the wanted values using selective
    //    access
    // 5. If the meter doesn't support selective access, then filter the retrieved values
    // 6. Convert the retrieved values to DTO's (includes adding the right unit and timestamp)

    final String privateOrPublic = request.getProfileType();

    // Determine which profiles (and values) to read, based on the configuration for the protocol of
    // the device and if the request is for private or public data.
    final Map<CosemObject, List<CosemObject>> profiles =
        this.getProfilesToRead(privateOrPublic, device);

    final GetPowerQualityProfileResponseDto response = new GetPowerQualityProfileResponseDto();
    final List<PowerQualityProfileDataDto> responseDatas = new ArrayList<>();

    for (final Map.Entry<CosemObject, List<CosemObject>> entry : profiles.entrySet()) {
      final CosemObject profile = entry.getKey();
      final List<CosemObject> configObjects = entry.getValue();

      final ObisCode obisCode = new ObisCode(profile.getObis());
      final DateTime beginDateTime = toDateTime(request.getBeginDate(), device.getTimezone());
      final DateTime endDateTime = toDateTime(request.getEndDate(), device.getTimezone());

      // All values that can be selected based on the info in the meter
      final List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);

      // Determine which values should be selected.
      // Note that if selective access is not supported, more values could be retrieved from the
      // meter. This list can then be used to filter the retrieved values. A LinkedHashMap is used
      // to preserve the order of entries (which will be used in the handler for selective access).
      final LinkedHashMap<Integer, SelectableObject> selectableObjects =
          this.determineSelectableObjects(captureObjects, configObjects);

      // Get the values from the buffer in the meter
      final List<GetResult> bufferList =
          this.retrieveBuffer(
              conn,
              device,
              obisCode,
              beginDateTime,
              endDateTime,
              new ArrayList<>(selectableObjects.values()));

      // Convert the retrieved values (e.g. add timestamps and add unit) and apply filter if needed
      final PowerQualityProfileDataDto responseDataDto =
          this.processData(profile, captureObjects, selectableObjects, bufferList, privateOrPublic);

      responseDatas.add(responseDataDto);
    }

    response.setPowerQualityProfileDatas(responseDatas);

    return response;
  }

  private List<CosemObject> getObjectsFromConfig(final DlmsDevice device, final CosemObject profile)
      throws ProtocolAdapterException {
    final List<String> tags = profile.getListProperty(ObjectProperty.SELECTABLE_OBJECTS);
    final List<DlmsObjectType> dlmsObjectTypes =
        tags.stream().map(DlmsObjectType::valueOf).toList();
    try {
      return this.objectConfigService.getCosemObjects(
          device.getProtocolName(), device.getProtocolVersion(), dlmsObjectTypes);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Error in object config", e);
    }
  }

  private Map<CosemObject, List<CosemObject>> getProfilesToRead(
      final String privateOrPublic, final DlmsDevice device) throws ProtocolAdapterException {
    final Map<CosemObject, List<CosemObject>> profilesWithObjects = new HashMap<>();

    this.addToMapIfNeeded(DEFINABLE_LOAD_PROFILE, privateOrPublic, device, profilesWithObjects);
    this.addToMapIfNeeded(POWER_QUALITY_PROFILE_1, privateOrPublic, device, profilesWithObjects);
    this.addToMapIfNeeded(POWER_QUALITY_PROFILE_2, privateOrPublic, device, profilesWithObjects);

    return profilesWithObjects;
  }

  private void addToMapIfNeeded(
      final DlmsObjectType profileType,
      final String privateOrPublic,
      final DlmsDevice device,
      final Map<CosemObject, List<CosemObject>> profilesWithSelectableObjects)
      throws ProtocolAdapterException {
    final String protocol = device.getProtocolName();
    final String version = device.getProtocolVersion();

    try {
      final Optional<CosemObject> optionalCosemObject =
          this.objectConfigService.getOptionalCosemObject(protocol, version, profileType);
      if (optionalCosemObject.isEmpty()) {
        // Cosem object is not in profile, then skip it
        return;
      }
      final CosemObject profileObject = optionalCosemObject.get();

      // Get all selectable objects for this profile
      final List<CosemObject> selectableObjectsFromConfig =
          this.getObjectsFromConfig(device, profileObject);

      // Filter the list on private/public objects. The clock should always be added.
      final List<CosemObject> selectableObjects =
          selectableObjectsFromConfig.stream()
              .filter(
                  object ->
                      object.getClassId() == InterfaceClass.CLOCK.id()
                          || this.hasPeriodicPqProfile(device, object, privateOrPublic))
              .toList();

      // Use this profile when at least the clock object and one other object should be read
      if (selectableObjects.size() > 1) {
        profilesWithSelectableObjects.put(profileObject, selectableObjects);
      }
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Determining PQ profiles to read failed", e);
    }
  }

  private boolean hasPeriodicPqProfile(
      final DlmsDevice device, final CosemObject object, final String privateOrPublic) {
    if (object.getListProperty(ObjectProperty.PQ_REQUEST) != null
        && object
            .getListProperty(ObjectProperty.PQ_REQUEST)
            .contains(
                device.isPolyphase()
                    ? PowerQualityRequest.PERIODIC_PP.name()
                    : PowerQualityRequest.PERIODIC_SP.name())) {
      return ((String) object.getProperty(ObjectProperty.PQ_PROFILE)).equals(privateOrPublic);
    }
    return false;
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
      final List<SelectableObject> selectableObjects)
      throws ProtocolAdapterException {

    final DataObject selectableValues = this.convertSelectableCaptureObjects(selectableObjects);

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
      final CosemObject profile,
      final List<GetResult> captureObjects,
      final LinkedHashMap<Integer, SelectableObject> selectableCaptureObjects,
      final List<GetResult> bufferList,
      final String publicOrPrivate)
      throws ProtocolAdapterException {

    final List<CaptureObjectDto> captureObjectDtos =
        this.createSelectableCaptureObjects(
            captureObjects, new ArrayList<>(selectableCaptureObjects.values()));

    final List<ProfileEntryDto> profileEntryDtos =
        this.createProfileEntries(
            bufferList, selectableCaptureObjects, this.getIntervalInMinutes(profile));
    final ProfileTypeDto profileTypeDto = ProfileTypeDto.valueOf(publicOrPrivate);
    return new PowerQualityProfileDataDto(
        new ObisCodeValuesDto(profile.getObis()),
        captureObjectDtos,
        profileEntryDtos,
        profileTypeDto);
  }

  private List<ProfileEntryDto> createProfileEntries(
      final List<GetResult> bufferList,
      final LinkedHashMap<Integer, SelectableObject> selectableCaptureObjects,
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
      final List<GetResult> captureObjects, final List<SelectableObject> selectableCaptureObjects)
      throws ProtocolAdapterException {

    final List<CaptureObjectDto> captureObjectDtos = new ArrayList<>();
    for (final GetResult captureObjectResult : captureObjects) {
      final DataObject dataObject = captureObjectResult.getResultData();
      final List<DataObject> captureObjectList = dataObject.getValue();
      for (final DataObject object : captureObjectList) {
        final Optional<SelectableObject> selectableObject =
            this.matchSelectableObject(object, selectableCaptureObjects);

        if (selectableObject.isPresent()) {
          captureObjectDtos.add(
              this.makeCaptureObjectDto(object, selectableObject.get().getScalerUnit()));
        }
      }
    }
    return captureObjectDtos;
  }

  private Optional<SelectableObject> matchSelectableObject(
      final DataObject dataObject, final List<SelectableObject> selectableCaptureObjects)
      throws ProtocolAdapterException {
    final CosemObjectDefinitionDto cosemObjectDefinitionDto =
        this.dlmsHelper.readObjectDefinition(dataObject, CAPTURE_OBJECT);

    return selectableCaptureObjects.stream()
        .filter(
            selectableObject ->
                this.isDefinitionOfSameObject(cosemObjectDefinitionDto, selectableObject))
        .findFirst();
  }

  private boolean isDefinitionOfSameObject(
      final CosemObjectDefinitionDto cosemObjectDefinitionDto,
      final SelectableObject captureObjectDefinition) {

    final int classIdCosemObjectDefinition = cosemObjectDefinitionDto.getClassId();
    final byte[] obisBytesCosemObjectDefinition =
        cosemObjectDefinitionDto.getLogicalName().toByteArray();
    final byte attributeIndexCosemObjectDefinition =
        (byte) cosemObjectDefinitionDto.getAttributeIndex();
    final int dataIndexCosemObjectDefinition = cosemObjectDefinitionDto.getDataIndex();
    final int classIdCaptureObjectDefinition = captureObjectDefinition.getClassId();
    final byte[] obisBytesCaptureObjectDefinition = captureObjectDefinition.getObisAsBytes();
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

  private CaptureObjectDto makeCaptureObjectDto(
      final DataObject captureObjectDataObject, final String scalerUnit)
      throws ProtocolAdapterException {

    final CosemObjectDefinitionDto cosemObjectDefinitionDto =
        this.dlmsHelper.readObjectDefinition(captureObjectDataObject, CAPTURE_OBJECT);

    return new CaptureObjectDto(
        cosemObjectDefinitionDto.getClassId(),
        cosemObjectDefinitionDto.getLogicalName().toString(),
        cosemObjectDefinitionDto.getAttributeIndex(),
        cosemObjectDefinitionDto.getDataIndex(),
        this.getUnit(scalerUnit));
  }

  private String getUnit(final String scalerUnit) {
    final DlmsUnitTypeDto unitType = this.getUnitType(scalerUnit);
    return unitType.getUnit();
  }

  private DlmsUnitTypeDto getUnitType(final String scalerUnit) {
    if (scalerUnit != null) {
      final String[] scalerUnitParts = scalerUnit.split(",");
      final DlmsUnitTypeDto unitType = DlmsUnitTypeDto.getUnitType(scalerUnitParts[1].trim());
      if (unitType != null) {
        return unitType;
      }
    }
    return DlmsUnitTypeDto.UNDEFINED;
  }

  protected ProfileEntryValueDto makeProfileEntryValueDto(
      final DataObject dataObject,
      final SelectableObject selectableObject,
      final ProfileEntryDto previousProfileEntryDto,
      final int timeInterval) {
    if (InterfaceClass.CLOCK.id() == selectableObject.getClassId()) {
      return this.makeDateProfileEntryValueDto(dataObject, previousProfileEntryDto, timeInterval);
    } else if (InterfaceClass.GSM_DIAGNOSTIC.id() == selectableObject.getClassId()) {
      return this.makeGsmDiagnosticProfileEntryValueDto(dataObject, selectableObject);
    } else if (dataObject.isNumber()) {
      return this.createNumericProfileEntryValueDto(dataObject, selectableObject);
    } else if (dataObject.isNull()) {
      return new ProfileEntryValueDto(null);
    } else {
      final String dbgInfo = this.dlmsHelper.getDebugInfo(dataObject);
      LOGGER.debug("creating ProfileEntryDto from {} {} ", dbgInfo, selectableObject);
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

  private ProfileEntryValueDto makeGsmDiagnosticProfileEntryValueDto(
      final DataObject dataObject, final SelectableObject selectableObject) {

    try {
      if (selectableObject.attributeIndex == GsmDiagnosticAttribute.CELL_INFO.attributeId()) {
        if (selectableObject.dataIndex == DATA_INDEX_SIGNAL_QUALITY) {
          final int value = this.dlmsHelper.readLong(dataObject, "Read signal quality").intValue();
          final SignalQualityDto signalQuality = SignalQualityDto.fromIndexValue(value);
          return new ProfileEntryValueDto(signalQuality.value());
        } else if (selectableObject.dataIndex == DATA_INDEX_BER) {
          final int value = this.dlmsHelper.readLong(dataObject, "Read ber").intValue();
          final BitErrorRateDto ber = BitErrorRateDto.fromIndexValue(value);
          return new ProfileEntryValueDto(ber.value());
        }
      }
    } catch (final ProtocolAdapterException | IllegalArgumentException e) {
      LOGGER.error("Error creating ProfileEntryDto from {}", dataObject, e);
    }

    final String debugInfo = this.dlmsHelper.getDebugInfo(dataObject);
    return new ProfileEntryValueDto(debugInfo);
  }

  private ProfileEntryValueDto createNumericProfileEntryValueDto(
      final DataObject dataObject, final SelectableObject selectableObject) {
    try {
      if (selectableObject.getScalerUnit() != null) {
        final DlmsMeterValueDto meterValue =
            this.dlmsHelper.getScaledMeterValueWithScalerUnit(
                dataObject, selectableObject.getScalerUnit(), "getScaledMeterValue");
        if (DlmsUnitTypeDto.COUNT.equals(this.getUnitType(selectableObject.getScalerUnit()))) {

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

  private LinkedHashMap<Integer, SelectableObject> determineSelectableObjects(
      final List<GetResult> captureObjects, final List<CosemObject> cosemConfigObjects)
      throws ProtocolAdapterException {

    final LinkedHashMap<Integer, SelectableObject> selectableObjects = new LinkedHashMap<>();

    // there is always only one GetResult
    for (final GetResult captureObjectResult : captureObjects) {

      final List<DataObject> dataObjects = captureObjectResult.getResultData().getValue();

      for (int positionInDataObjectsList = 0;
          positionInDataObjectsList < dataObjects.size();
          positionInDataObjectsList++) {

        final DataObject dataObject = dataObjects.get(positionInDataObjectsList);

        final CosemObjectDefinitionDto objectDefinition =
            this.dlmsHelper.readObjectDefinition(dataObject, CAPTURE_OBJECT);

        final String obis = objectDefinition.getLogicalName().toString();
        final Optional<CosemObject> matchedCosemObject =
            cosemConfigObjects.stream()
                .filter(obj -> this.obisMatches(obis, obj.getObis()))
                .findFirst();

        if (matchedCosemObject.isPresent()) {
          selectableObjects.put(
              positionInDataObjectsList,
              new SelectableObject(
                  objectDefinition.getClassId(),
                  obis,
                  (byte) objectDefinition.getAttributeIndex(),
                  objectDefinition.getDataIndex(),
                  this.getScalerUnit(matchedCosemObject.get())));
        }
      }
    }

    return selectableObjects;
  }

  private boolean obisMatches(final String captureObis, final String configObis) {
    List<String> obisCodesToMatch = Collections.singletonList(configObis);

    // An obis with an x indicates that multiple objects could exist for the m-bus channels
    if (configObis.contains("x")) {
      obisCodesToMatch = CHANNELS.stream().map(i -> configObis.replace("x", i.toString())).toList();
    }

    return obisCodesToMatch.contains(captureObis);
  }

  private String getScalerUnit(final CosemObject object) {
    if (object.getClassId() == InterfaceClass.REGISTER.id()) {
      return object.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId()).getValue();
    } else if (object.getClassId() == InterfaceClass.EXTENDED_REGISTER.id()) {
      return object.getAttribute(ExtendedRegisterAttribute.SCALER_UNIT.attributeId()).getValue();
    } else {
      return null;
    }
  }

  private int getIntervalInMinutes(final CosemObject object) throws ProtocolAdapterException {
    try {
      return Integer.parseInt(object.getAttribute(ATTRIBUTE_ID_INTERVAL).getValue()) / 60;
    } catch (final Exception e) {
      throw new ProtocolAdapterException("Error in interval in object config", e);
    }
  }

  @Getter
  protected static class SelectableObject {
    private final int classId;
    private final String logicalName;
    private final byte attributeIndex;
    private final Integer dataIndex;
    private final String scalerUnit;

    public SelectableObject(
        final int classId,
        final String logicalName,
        final byte attributeIndex,
        final Integer dataIndex,
        final String scalerUnit) {
      this.classId = classId;
      this.logicalName = logicalName;
      this.attributeIndex = attributeIndex;
      this.dataIndex = dataIndex;
      this.scalerUnit = scalerUnit;
    }

    public byte[] getObisAsBytes() {
      final ObisCodeValuesDto obisCodeValuesDto = new ObisCodeValuesDto(this.logicalName);
      return obisCodeValuesDto.toByteArray();
    }
  }
}
