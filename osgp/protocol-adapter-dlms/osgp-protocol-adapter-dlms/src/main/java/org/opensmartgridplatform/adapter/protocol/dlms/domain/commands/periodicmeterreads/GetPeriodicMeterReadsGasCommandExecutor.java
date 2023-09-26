// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.BUFFER;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.CAPTURE_OBJECTS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.CAPTURE_PERIOD;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AMR_PROFILE_STATUS;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AMR_PROFILE_STATUS_DAILY_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AMR_PROFILE_STATUS_HOURLY_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AMR_PROFILE_STATUS_MONTHLY_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.CLOCK;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DAILY_VALUES_COMBINED;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DAILY_VALUES_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.INTERVAL_VALUES_G;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_MASTER_VALUE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MONTHLY_VALUES_COMBINED;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MONTHLY_VALUES_G;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
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
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetPeriodicMeterReadsGasCommandExecutor
    extends AbstractPeriodicMeterReadsCommandExecutor<
        PeriodicMeterReadsRequestDto, PeriodicMeterReadGasResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

  private static final String GAS_VALUE = "gasValue";
  private static final String PERIODIC_G_METER_READS = "Periodic G-Meter Reads";
  private static final String UNEXPECTED_VALUE =
      "Unexpected null/unspecified value for Gas Capture Time";
  private static final String FORMAT_DESCRIPTION =
      "GetPeriodicMeterReadsGas for channel %s, %s from %s until %s, " + "retrieve attribute: %s";

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigService objectConfigService;

  @Autowired
  public GetPeriodicMeterReadsGasCommandExecutor(
      final DlmsHelper dlmsHelper,
      final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
      final ObjectConfigService objectConfigService) {
    super(PeriodicMeterReadsGasRequestDto.class, amrProfileStatusCodeHelper);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigService = objectConfigService;
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
    final boolean selectedValuesSupported = device.isSelectiveAccessPeriodicMeterReadsSupported();
    final int channel = periodicMeterReadsQuery.getChannel().getChannelNumber();

    final CosemObject profileObject = this.getProfileConfigObject(device, queryPeriodType);

    final List<CaptureObject> allCaptureObjectsInProfile =
        this.getCaptureObjectsInProfile(profileObject, device, channel);

    final List<CaptureObject> selectedCaptureObjects =
        this.getSelectedCaptureObjects(
            allCaptureObjectsInProfile, Medium.GAS, channel, selectedValuesSupported);

    final boolean selectValues =
        selectedValuesSupported
            && (allCaptureObjectsInProfile.size() != selectedCaptureObjects.size());

    final AttributeAddress profileBufferAddress =
        this.getAttributeAddressForProfile(
            profileObject, from, to, channel, selectedCaptureObjects, selectValues);

    final ProfileCaptureTime intervalTime = this.getProfileCaptureTime(profileObject);

    LOGGER.info(
        "Retrieving current billing period and profiles for gas for period type: {}, from: "
            + "{}, to: {}",
        queryPeriodType,
        from,
        to);

    conn.getDlmsMessageListener()
        .setDescription(
            String.format(
                FORMAT_DESCRIPTION,
                periodicMeterReadsQuery.getChannel(),
                queryPeriodType,
                from,
                to,
                JdlmsObjectToStringUtil.describeAttributes(profileBufferAddress)));

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn,
            device,
            "retrieve periodic meter reads for " + queryPeriodType + ", channel " + channel,
            profileBufferAddress);

    LOGGER.debug("Received getResult: {} ", getResultList);

    final DataObject resultData =
        this.dlmsHelper.readDataObject(getResultList.get(0), PERIODIC_G_METER_READS);
    final List<DataObject> bufferedObjectsList = resultData.getValue();

    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads = new ArrayList<>();
    for (final DataObject bufferedObject : bufferedObjectsList) {
      final List<DataObject> bufferedObjectValue = bufferedObject.getValue();

      try {
        periodicMeterReads.add(
            this.convertToResponseItem(
                queryPeriodType,
                selectedCaptureObjects,
                intervalTime,
                bufferedObjectValue,
                channel,
                periodicMeterReads));
      } catch (final BufferedDateTimeValidationException e) {
        LOGGER.warn(e.getMessage(), e);
      }
    }

    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReadsWithinRequestedPeriod =
        periodicMeterReads.stream()
            .filter(
                meterRead ->
                    this.validateDateTime(meterRead.getLogTime(), from.toDate(), to.toDate()))
            .toList();

    LOGGER.debug("Resulting periodicMeterReads: {} ", periodicMeterReads);

    return new PeriodicMeterReadGasResponseDto(
        queryPeriodType, periodicMeterReadsWithinRequestedPeriod);
  }

  private PeriodicMeterReadsGasResponseItemDto convertToResponseItem(
      final PeriodTypeDto periodType,
      final List<CaptureObject> selectedObjects,
      final ProfileCaptureTime intervalTime,
      final List<DataObject> bufferedObjects,
      final int channel,
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final Optional<Date> previousLogTime = this.getPreviousLogTime(periodicMeterReads);
    final Date logTime =
        this.readClock(
            periodType,
            selectedObjects,
            previousLogTime,
            intervalTime,
            bufferedObjects,
            this.dlmsHelper);

    final AmrProfileStatusCodeDto status =
        this.readStatus(bufferedObjects, selectedObjects, periodType);
    final DataObject gasValue = this.readValue(bufferedObjects, selectedObjects, channel);

    final String scalerUnit = this.getScalerUnit(selectedObjects, channel);

    final Optional<Date> previousCaptureTime = this.getPreviousCaptureTime(periodicMeterReads);
    final Date captureTime =
        this.readCaptureTime(
            bufferedObjects, selectedObjects, previousCaptureTime, periodType, intervalTime);

    LOGGER.debug("Converting bufferObject with value: {} ", bufferedObjects);
    LOGGER.debug(
        "Resulting values: LogTime: {}, status: {}, gasValue {}, scalerUnit: {}, captureTime {} ",
        logTime,
        status,
        gasValue,
        scalerUnit,
        captureTime);

    return new PeriodicMeterReadsGasResponseItemDto(
        logTime,
        this.dlmsHelper.getScaledMeterValueWithScalerUnit(gasValue, scalerUnit, GAS_VALUE),
        captureTime,
        status);
  }

  private Optional<Date> getPreviousLogTime(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads) {

    if (periodicMeterReads.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(periodicMeterReads.get(periodicMeterReads.size() - 1).getLogTime());
  }

  private Optional<Date> getPreviousCaptureTime(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads) {

    if (periodicMeterReads.isEmpty()) {
      return Optional.empty();
    }

    final PeriodicMeterReadsGasResponseItemDto meterRead =
        periodicMeterReads.get(periodicMeterReads.size() - 1);

    if (meterRead.getCaptureTime() == null) {
      return Optional.empty();
    }

    return Optional.of(meterRead.getCaptureTime());
  }

  private Date readClock(
      final PeriodTypeDto periodType,
      final List<CaptureObject> selectedObjects,
      final Optional<Date> previousLogTime,
      final ProfileCaptureTime intervalTime,
      final List<DataObject> bufferedObjects,
      final DlmsHelper dlmsHelper)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final Date logTime;

    final Integer clockIndex = this.getIndex(selectedObjects, CLOCK, null);

    CosemDateTimeDto cosemDateTime = null;

    if (clockIndex != null) {
      cosemDateTime =
          dlmsHelper.readDateTime(
              bufferedObjects.get(clockIndex), "Clock from " + periodType + " buffer");
    }

    final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

    if (bufferedDateTime != null) {
      logTime = bufferedDateTime.toDate();
    } else {
      logTime =
          this.calculateIntervalTimeBasedOnPreviousValue(
              periodType, previousLogTime, Optional.of(intervalTime));
    }

    if (logTime == null) {
      throw new BufferedDateTimeValidationException("Unable to calculate logTime");
    }

    return logTime;
  }

  AmrProfileStatusCodeDto readStatus(
      final List<DataObject> bufferedObjects,
      final List<CaptureObject> selectedObjects,
      final PeriodTypeDto periodType)
      throws ProtocolAdapterException {

    final DlmsObjectType amrStatus =
        switch (periodType) {
          case INTERVAL -> AMR_PROFILE_STATUS_HOURLY_G;
          case DAILY -> AMR_PROFILE_STATUS_DAILY_G;
          case MONTHLY -> AMR_PROFILE_STATUS_MONTHLY_G;
        };

    Integer statusIndex = this.getIndex(selectedObjects, amrStatus, null);

    if (statusIndex == null) {
      // If combined profiles are used, get the index for the general amr status
      statusIndex = this.getIndex(selectedObjects, AMR_PROFILE_STATUS, null);
    }

    AmrProfileStatusCodeDto amrProfileStatusCode = null;

    if (statusIndex != null) {
      amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects.get(statusIndex));
    }

    return amrProfileStatusCode;
  }

  public Integer getIndex(
      final List<CaptureObject> selectedObjects,
      final DlmsObjectType type,
      final Integer attributeId)
      throws ProtocolAdapterException {
    return this.getIndex(selectedObjects, type, attributeId, null);
  }

  public Integer getIndex(
      final List<CaptureObject> selectedObjects,
      final DlmsObjectType type,
      final Integer attributeId,
      final Integer channel)
      throws ProtocolAdapterException {
    int index = 0;

    try {
      for (final CaptureObject object : selectedObjects) {
        if (object.getCosemObject().getTag().equals(type.name())
            && (attributeId == null || object.getAttributeId() == attributeId)
            && (channel == null || object.getCosemObject().getChannel() == channel)) {
          return index;
        }
        index++;
      }
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Can't get index for " + type.name(), e);
    }

    return null;
  }

  private DataObject readValue(
      final List<DataObject> bufferedObjects,
      final List<CaptureObject> selectedObjects,
      final int channel)
      throws ProtocolAdapterException {

    final Integer valueIndex = this.getIndex(selectedObjects, MBUS_MASTER_VALUE, 2, channel);

    DataObject value = null;

    if (valueIndex != null) {
      value = bufferedObjects.get(valueIndex);
    }

    return value;
  }

  private Date readCaptureTime(
      final List<DataObject> bufferedObjects,
      final List<CaptureObject> selectedObjects,
      final Optional<Date> previousCaptureTime,
      final PeriodTypeDto periodType,
      final ProfileCaptureTime intervalTime)
      throws ProtocolAdapterException, BufferedDateTimeValidationException {

    final Integer captureTimeIndex =
        this.getIndex(selectedObjects, DlmsObjectType.MBUS_MASTER_VALUE, 5);

    if (captureTimeIndex != null) {
      final CosemDateTimeDto cosemDateTime =
          this.dlmsHelper.readDateTime(
              bufferedObjects.get(captureTimeIndex), "Clock from mbus interval extended register");

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

    return null;
  }

  private String getScalerUnit(final List<CaptureObject> selectedObjects, final int channel)
      throws ProtocolAdapterException {

    final Integer index = this.getIndex(selectedObjects, MBUS_MASTER_VALUE, 2, channel);

    if (index != null) {
      return selectedObjects.get(index).getCosemObject().getAttribute(3).getValue();
    } else {
      throw new ProtocolAdapterException("Can't get scaler unit, selected object not found");
    }
  }

  private CosemObject getProfileConfigObject(
      final DlmsDevice device, final PeriodTypeDto periodType) throws ProtocolAdapterException {
    final String protocol = device.getProtocolName();
    final String version = device.getProtocolVersion();

    try {
      return switch (periodType) {
        case DAILY -> {
          final Optional<CosemObject> optionalDaily =
              this.objectConfigService.getOptionalCosemObject(protocol, version, DAILY_VALUES_G);
          if (optionalDaily.isPresent()) {
            yield optionalDaily.get();
          } else {
            yield this.objectConfigService.getCosemObject(protocol, version, DAILY_VALUES_COMBINED);
          }
        }
        case MONTHLY -> {
          final Optional<CosemObject> optionalMonthly =
              this.objectConfigService.getOptionalCosemObject(protocol, version, MONTHLY_VALUES_G);
          if (optionalMonthly.isPresent()) {
            yield optionalMonthly.get();
          } else {
            yield this.objectConfigService.getCosemObject(
                protocol, version, MONTHLY_VALUES_COMBINED);
          }
        }
        case INTERVAL -> this.objectConfigService.getCosemObject(
            protocol, version, INTERVAL_VALUES_G);
      };
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(
          "Can't find profile object in "
              + protocol
              + " "
              + version
              + " config for "
              + periodType.name()
              + " values",
          e);
    }
  }

  private AttributeAddress getAttributeAddressForProfile(
      final CosemObject profile,
      final DateTime from,
      final DateTime to,
      final int channel,
      final List<CaptureObject> selectedCaptureObjects,
      final boolean selectValues) {

    final SelectiveAccessDescription access =
        this.getAccessDescription(selectedCaptureObjects, from, to, selectValues);

    final ObisCode obisCode = new ObisCode(profile.getObis().replace("x", String.valueOf(channel)));

    return new AttributeAddress(profile.getClassId(), obisCode, BUFFER.attributeId(), access);
  }

  private List<CaptureObject> getCaptureObjectsInProfile(
      final CosemObject profile, final DlmsDevice device, final Integer channel)
      throws ProtocolAdapterException {
    final Attribute captureObjectsAttribute = profile.getAttribute(CAPTURE_OBJECTS.attributeId());
    final List<String> captureObjectDefinitions =
        List.of(captureObjectsAttribute.getValue().split("\\|"));

    final List<CaptureObject> captureObjects = new ArrayList<>();

    final List<Integer> channels;
    try {
      if (profile.hasWildcardChannel()) {
        // If the profile has an x for the channel in the obis code, then we will request the
        // profile with the channel specified in the request. All capture objects then have the
        // same channel.
        channels = List.of(channel);
      } else {
        // If the profile has no x for channel, then for each capture objects with an x in the
        // config should be handled as 4 different capture objects, one for each channel 1..4.
        channels = List.of(1, 2, 3, 4);
      }
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Could not get channel from obis " + profile.getObis(), e);
    }

    final List<CaptureObject> captureObjectsWithWildcardChannel = new ArrayList<>();
    final List<CaptureObject> captureObjectsWithoutWildcardChannel = new ArrayList<>();
    for (final String def : captureObjectDefinitions) {
      final CaptureObject captureObject = this.getCaptureObject(def, device);
      try {
        if (captureObject.getCosemObject().hasWildcardChannel()) {
          captureObjectsWithWildcardChannel.add(captureObject);
        } else {
          captureObjectsWithoutWildcardChannel.add(captureObject);
        }
      } catch (final ObjectConfigException e) {
        throw new ProtocolAdapterException(
            "Could not get channel from obis " + captureObject.getCosemObject(), e);
      }
    }

    captureObjects.addAll(captureObjectsWithoutWildcardChannel);

    captureObjects.addAll(
        channels.stream()
            .map(c -> this.addChannel(captureObjectsWithWildcardChannel, c))
            .flatMap(List::stream)
            .toList());

    return captureObjects;
  }

  private List<CaptureObject> getSelectedCaptureObjects(
      final List<CaptureObject> allCaptureObjectsInProfile,
      final Medium medium,
      final int channel,
      final boolean selectedValuesSupported)
      throws ProtocolAdapterException {

    final List<CaptureObject> selectedObjects = new ArrayList<>();

    try {
      for (final CaptureObject captureObject : allCaptureObjectsInProfile) {
        // If selectedValues is supported, then select all capture objects with the same medium and
        // the same channel. All abstract objects (clock and amr status) should be selected as well.
        if (!selectedValuesSupported
            || (captureObject.getCosemObject().getGroup().equals(medium.name())
                && captureObject.getCosemObject().getChannel() == channel)
            || (captureObject.getCosemObject().getGroup().equals(Medium.ABSTRACT.name()))) {
          selectedObjects.add(captureObject);
        }
      }
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Can't get selected capture objects", e);
    }

    return selectedObjects;
  }

  private SelectiveAccessDescription getAccessDescription(
      final List<CaptureObject> selectedCaptureObjects,
      final DateTime from,
      final DateTime to,
      final boolean selectValues) {

    if (from == null || to == null) {
      return null;
    } else {
      final int accessSelector = 1;

      final DataObject selectedValues = this.getSelectedValuesObject(selectedCaptureObjects);

      final DataObject accessParameter =
          this.dlmsHelper.getAccessSelectionTimeRangeParameter(
              from,
              to,
              selectValues ? selectedValues : DataObject.newArrayData(Collections.emptyList()));

      return new SelectiveAccessDescription(accessSelector, accessParameter);
    }
  }

  private DataObject getSelectedValuesObject(final List<CaptureObject> selectedObjects) {
    final List<DataObject> objectDefinitions = this.getObjectDefinitions(selectedObjects);
    return DataObject.newArrayData(objectDefinitions);
  }

  private List<DataObject> getObjectDefinitions(final List<CaptureObject> selectedObjects) {
    final List<DataObject> objectDefinitions = new ArrayList<>();

    for (final CaptureObject captureObject : selectedObjects) {
      final CosemObject relatedObject = captureObject.getCosemObject();
      objectDefinitions.add(
          DataObject.newStructureData(
              Arrays.asList(
                  DataObject.newUInteger16Data(relatedObject.getClassId()),
                  DataObject.newOctetStringData(new ObisCode(relatedObject.getObis()).bytes()),
                  DataObject.newInteger8Data((byte) captureObject.getAttributeId()),
                  DataObject.newUInteger16Data(0))));
    }

    return objectDefinitions;
  }

  private CaptureObject getCaptureObject(
      final String captureObjectDefinition, final DlmsDevice device)
      throws ProtocolAdapterException {
    final String protocol = device.getProtocolName();
    final String version = device.getProtocolVersion();

    final String[] captureObjectDefinitionParts = captureObjectDefinition.split(",");
    final DlmsObjectType objectType = DlmsObjectType.fromValue(captureObjectDefinitionParts[0]);
    final int attributeId = Integer.parseInt(captureObjectDefinitionParts[1]);

    try {
      final CosemObject cosemObject =
          this.objectConfigService.getCosemObject(protocol, version, objectType);
      return new CaptureObject(cosemObject, attributeId);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(
          "Capture object " + captureObjectDefinition + " not found in object config", e);
    }
  }

  private List<CaptureObject> addChannel(
      final List<CaptureObject> captureObjects, final int channel) {
    final List<CaptureObject> captureObjectsWithChannel = new ArrayList<>();

    for (final CaptureObject captureObject : captureObjects) {
      final CosemObject cosemObject = captureObject.getCosemObject();
      captureObjectsWithChannel.add(
          new CaptureObject(
              this.updateCosemObjectWithChannel(cosemObject, channel),
              captureObject.getAttributeId()));
    }

    return captureObjectsWithChannel;
  }

  private List<CaptureObject> addChannels(
      final CaptureObject captureObject, final List<Integer> channels) {
    final CosemObject cosemObject = captureObject.getCosemObject();
    if (!cosemObject.getObis().contains("x")) {
      return List.of(captureObject);
    } else {
      return channels.stream()
          .map(
              c ->
                  new CaptureObject(
                      this.updateCosemObjectWithChannel(cosemObject, c),
                      captureObject.getAttributeId()))
          .toList();
    }
  }

  private List<CaptureObject> addChannels(
      final CaptureObject captureObject, final int channel, final boolean selectedValuesSupported) {
    final CosemObject cosemObject = captureObject.getCosemObject();
    if (!cosemObject.getObis().contains("x")) {
      return List.of(captureObject);
    } else if (selectedValuesSupported) {
      return List.of(
          new CaptureObject(
              this.updateCosemObjectWithChannel(cosemObject, channel),
              captureObject.getAttributeId()));
    } else {
      return IntStream.of(1, 2, 3, 4)
          .mapToObj(
              c ->
                  new CaptureObject(
                      this.updateCosemObjectWithChannel(cosemObject, c),
                      captureObject.getAttributeId()))
          .toList();
    }
  }

  private CosemObject updateCosemObjectWithChannel(
      final CosemObject cosemObject, final int channel) {
    final CosemObject cosemObjectWithChannel = new CosemObject();
    cosemObjectWithChannel.setObis(cosemObject.getObis().replace("x", String.valueOf(channel)));
    cosemObjectWithChannel.setGroup(cosemObject.getGroup());
    cosemObjectWithChannel.setDescription(cosemObject.getDescription());
    cosemObjectWithChannel.setMeterTypes(cosemObject.getMeterTypes());
    cosemObjectWithChannel.setNote(cosemObject.getNote());
    cosemObjectWithChannel.setAttributes(cosemObject.getAttributes());
    cosemObjectWithChannel.setClassId(cosemObject.getClassId());
    cosemObjectWithChannel.setTag(cosemObject.getTag());
    cosemObjectWithChannel.setProperties(cosemObject.getProperties());
    cosemObjectWithChannel.setVersion(cosemObject.getVersion());
    return cosemObjectWithChannel;
  }

  private ProfileCaptureTime getProfileCaptureTime(final CosemObject profile)
      throws ProtocolAdapterException {

    final Attribute capturePeriodAttribute = profile.getAttribute(CAPTURE_PERIOD.attributeId());
    final String capturePeriodValue = capturePeriodAttribute.getValue();

    return switch (capturePeriodValue) {
      case "3600" -> ProfileCaptureTime.HOUR;
      case "86400" -> ProfileCaptureTime.DAY;
      case "0" -> ProfileCaptureTime.MONTH;
      default -> throw new ProtocolAdapterException(
          "Unexpected capture period " + capturePeriodValue);
    };
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }
}
