// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.BUFFER;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
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
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ProfileGeneric;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;
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

    final DateTime from =
        DlmsDateTimeConverter.toDateTime(
            periodicMeterReadsQuery.getBeginDate(), device.getTimezone());
    final DateTime to =
        DlmsDateTimeConverter.toDateTime(
            periodicMeterReadsQuery.getEndDate(), device.getTimezone());

    // The query can be for 3 different types of values: Interval (hourly), daily or monthly.
    final PeriodTypeDto queryPeriodType = periodicMeterReadsQuery.getPeriodType();

    // The periodic values are stored in the meter in the buffer of a Profile Generic.
    // Based on the type and the protocol version, get the information for the right Profile from
    // the object configuration.
    final ProfileGeneric profileObject = this.getProfileConfigObject(device, queryPeriodType);

    // A Profile Generic periodically stores values of multiple objects. Usually, this is a
    // timestamp from the Clock, a status, a meter value and the timestamp when the meter value was
    // stored (the capture time). For some meters, the gas values and the electricity values are
    // combined in one profile.
    // Values in the profile will be selected using selective access. Selecting values based on a
    // start and end datetime should be supported for all devices. Selecting a subset of values to
    // be retrieved (e.g. only gas values in a combined profile) is not supported by all devices.
    final boolean selectedValuesSupported = device.isSelectiveAccessPeriodicMeterReadsSupported();

    // A request can be for channel 1-4.
    final int channel = periodicMeterReadsQuery.getChannel().getChannelNumber();

    // The profile object from the object config contains information about the objects for which
    // a value (one of the attributes) is stored in the profile: the capture objects.
    // Note that the order is important: the meter will return the values in the order of the
    // capture object definition in the profile.
    // All capture objects are retrieved from the config as well to get information about the scaler
    // and unit of the values.
    final String deviceModelCode =
        this.getDeviceModelCodeOfChannel(messageMetadata.getDeviceModelCode(), channel);
    final List<CaptureObject> allCaptureObjectsInProfile =
        this.getCaptureObjectsInProfile(profileObject, device, channel, deviceModelCode);

    // If it selectedValues is supported, then determine a subset of capture objects that are to be
    // retrieved. E.g. when it is a combined profile, we can only get the gas values without the
    // electricity values. This is more efficient and improves privacy (we only get what we need).
    final List<CaptureObject> selectedCaptureObjects =
        this.getSelectedCaptureObjects(
            allCaptureObjectsInProfile, Medium.GAS, channel, selectedValuesSupported);

    // Check if it's needed to select values. If all values are requested, then we can skip the
    // select values option in the request to the meter.
    final boolean selectValues =
        selectedValuesSupported
            && (allCaptureObjectsInProfile.size() != selectedCaptureObjects.size());

    // To request the values from the meter, we need the address. This contains the obis code of
    // the profile object, the attribute id of the buffer (2) and the selective access parameters:
    // the from and to dates and (if applicable) the selected values.
    final AttributeAddress profileBufferAddress =
        this.getAttributeAddressForProfile(
            profileObject, from, to, channel, selectedCaptureObjects, selectValues);

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

    // This is the actual request to the meter. The DlmsHelper will automatically check if the
    // result is SUCCESS. Otherwise, it will throw an exception.
    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn,
            device,
            "retrieve periodic meter reads for " + queryPeriodType + ", channel " + channel,
            profileBufferAddress);

    LOGGER.debug("Received getResult: {} ", getResultList);

    // Unpack the data from the meter response
    final DataObject resultData =
        this.dlmsHelper.readDataObject(getResultList.get(0), PERIODIC_G_METER_READS);
    final List<DataObject> bufferedObjectsList = resultData.getValue();

    // The values in the bufferedObjectList now need to be converted to a ResponseItem including
    // information about the time, the type of value and the unit.

    // A capture object might not have a fixed scaler unit in the config, or the scaler unit needs
    // to be chosen based on the device type. So check if that is the case and update the capture
    // objects if necessary. Note: this might result in an additional request to the meter.
    final List<CaptureObject> captureObjectsWithScalerUnit =
        this.checkAndGetScalerUnits(selectedCaptureObjects, conn, device, Medium.GAS, channel);

    // The interval time of the profile is important. For efficiency, most meters only send a
    // timestamp for the first value in the response. The timestamp of the other values should be
    // calculated using the interval time.
    final ProfileCaptureTime intervalTime = this.getProfileCaptureTime(profileObject);

    // Now convert the retrieved values. Each buffered object contains the values for a single
    // interval (e.g. a timestamp, a status and a meter value). The values in the buffered object
    // are in the order of the capture objects in the profile.
    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads = new ArrayList<>();
    for (final DataObject bufferedObject : bufferedObjectsList) {
      final List<DataObject> bufferedObjectValue = bufferedObject.getValue();

      try {
        periodicMeterReads.add(
            this.convertToResponseItem(
                queryPeriodType,
                captureObjectsWithScalerUnit,
                intervalTime,
                bufferedObjectValue,
                channel,
                periodicMeterReads));
      } catch (final BufferedDateTimeValidationException e) {
        LOGGER.warn(e.getMessage(), e);
      }
    }

    // To be sure no values are returned outside the requested period, filter on from and to date.
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

    // The bufferedObjects contain the values retrieved from the meter for a single interval.
    // The bufferedObjects contain no information about the type of value. But because the
    // bufferedObjects are always in the same known order (the order of the selectedObjects), we can
    // still convert the values.

    // The first timestamp in the response of a meter should always be included. The following
    // intervals could have a 'null' timestamp, meaning the time should be calculated based on the
    // previous timestamp.
    final Optional<Date> previousLogTime = this.getPreviousLogTime(periodicMeterReads);
    final Date logTime =
        this.readClock(
            periodType,
            selectedObjects,
            previousLogTime,
            intervalTime,
            bufferedObjects,
            this.dlmsHelper);

    // The status is used in most profiles. But for some it is not used. In that case, the
    // selectedObjects will not contain a status object and readStatus will return null.
    final AmrProfileStatusCodeDto status =
        this.readStatus(bufferedObjects, selectedObjects, periodType);

    // The gasValue should always be included. The value of the meter has no information about
    // the scaler or the unit, so that information is retrieved from the corresponding capture
    // object in the selected objects.
    final DataObject gasValue = this.readValue(bufferedObjects, selectedObjects, channel);
    final String scalerUnit = this.getScalerUnit(selectedObjects, channel);

    // The capture time is used in most profiles. But for some it is not used. In that case, the
    // selectedObjects will not contain a capture time object and readCaptureTime will return null.
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
      return ((Register) selectedObjects.get(index).getCosemObject()).getScalerUnit();
    } else {
      throw new ProtocolAdapterException("Can't get scaler unit, selected object not found");
    }
  }

  private ProfileGeneric getProfileConfigObject(
      final DlmsDevice device, final PeriodTypeDto periodType) throws ProtocolAdapterException {
    final CosemObject profile;

    final String protocol = device.getProtocolName();
    final String version = device.getProtocolVersion();

    try {
      profile =
          switch (periodType) {
            case DAILY -> {
              final Optional<CosemObject> optionalDaily =
                  this.objectConfigService.getOptionalCosemObject(
                      protocol, version, DAILY_VALUES_G);
              if (optionalDaily.isPresent()) {
                yield optionalDaily.get();
              } else {
                yield this.objectConfigService.getCosemObject(
                    protocol, version, DAILY_VALUES_COMBINED);
              }
            }
            case MONTHLY -> {
              final Optional<CosemObject> optionalMonthly =
                  this.objectConfigService.getOptionalCosemObject(
                      protocol, version, MONTHLY_VALUES_G);
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

    return (ProfileGeneric) profile;
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
      final ProfileGeneric profile,
      final DlmsDevice device,
      final Integer channel,
      final String deviceModel)
      throws ProtocolAdapterException {
    try {
      return profile.getCaptureObjects(
          this.objectConfigService,
          device.getProtocolName(),
          device.getProtocolVersion(),
          channel,
          deviceModel);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(
          "Could not get capture objects for profile " + profile.getTag(), e);
    }
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

  private List<CaptureObject> checkAndGetScalerUnits(
      final List<CaptureObject> captureObjects,
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Medium medium,
      final int channel)
      throws ProtocolAdapterException {
    final List<CaptureObject> captureObjectsWithScalerUnit = new ArrayList<>();

    // Each relevant meter value retrieved from the meter should have a scaler and unit. If values
    // were retrieved from a combined (E+G) profile and selectedValues is not supported, then
    // more values are retrieved than needed. For example: if the request was for gas and channel 1,
    // then all electricity values and values for other channels are not relevant and no scaler and
    // unit is needed for those objects. Note: order of the capture objects is important and
    // should not change.
    final List<CaptureObject> relevantCaptureObjects =
        this.getRelevantCaptureObjects(captureObjects, medium.name(), channel);

    final List<CaptureObject> captureObjectsThatNeedScalerUnitFromMeter = new ArrayList<>();

    for (final CaptureObject captureObject : relevantCaptureObjects) {
      final Register register = (Register) captureObject.getCosemObject();

      // There are 2 possibilities for the scalerUnit in the capture object:
      // - A fixed scalerUnit is defined. In that case, we don't need to do anything.
      // - No scalerUnit is defined or the scalerUnit is defined as Dynamic. In that case, the
      //   scaler unit needs to be read from the meter
      if (register.needsScalerUnitFromMeter()) {
        captureObjectsThatNeedScalerUnitFromMeter.add(captureObject);
      }
    }

    // Get scaler units from meter. They are read from the meter in one call for efficiency.
    final Map<CaptureObject, String> scalerUnits =
        new HashMap<>(
            this.getScalerUnitsFromMeter(captureObjectsThatNeedScalerUnitFromMeter, conn, device));

    // Create a new list with capture objects and fill in the missing scaler units.
    // Note: the order should be the same as the order of the capture objects in the input param.
    for (final CaptureObject captureObject : captureObjects) {
      if (scalerUnits.containsKey(captureObject)) {
        final CosemObject cosemObject = captureObject.getCosemObject();
        final Attribute scalerUnitAttribute =
            cosemObject.getAttribute(ExtendedRegisterAttribute.SCALER_UNIT.attributeId());

        final Attribute newScalerUnitAttribute =
            scalerUnitAttribute.copyWithNewValue(scalerUnits.get(captureObject));

        captureObjectsWithScalerUnit.add(
            captureObject.copyWithNewAttribute(newScalerUnitAttribute));
      } else {
        captureObjectsWithScalerUnit.add(captureObject);
      }
    }

    return captureObjectsWithScalerUnit;
  }

  private List<CaptureObject> getRelevantCaptureObjects(
      final List<CaptureObject> captureObjects, final String medium, final int channel) {
    return captureObjects.stream()
        .filter(captureObject -> captureObject.getCosemObject() instanceof Register)
        .filter(
            captureObject ->
                captureObject.getAttributeId() == ExtendedRegisterAttribute.VALUE.attributeId())
        .filter(captureObject -> captureObject.getCosemObject().getGroup().equals(medium))
        .filter(
            captureObject ->
                this.getChannelWithoutException(captureObject.getCosemObject()) == channel)
        .toList();
  }

  private int getChannelWithoutException(final CosemObject object) {
    try {
      return object.getChannel();
    } catch (final ObjectConfigException e) {
      return -1;
    }
  }

  private Map<CaptureObject, String> getScalerUnitsFromMeter(
      final List<CaptureObject> captureObjects,
      final DlmsConnectionManager conn,
      final DlmsDevice device)
      throws ProtocolAdapterException {
    if (captureObjects.isEmpty()) {
      return Map.of();
    }

    final Map<CaptureObject, String> captureObjectsWithScalerUnit = new HashMap<>();

    final AttributeAddress[] scalerUnitAddresses = this.getScalerUnitAddresses(captureObjects);

    conn.getDlmsMessageListener()
        .setDescription(
            String.format(
                "GetPeriodicMeterReadsGas scaler units, retrieve attribute: %s",
                JdlmsObjectToStringUtil.describeAttributes(scalerUnitAddresses)));

    final List<GetResult> getResults =
        this.dlmsHelper.getWithList(conn, device, scalerUnitAddresses);

    if (getResults.stream().anyMatch(result -> result.getResultCode() != AccessResultCode.SUCCESS)
        || getResults.size() != captureObjects.size()) {
      throw new ProtocolAdapterException(
          "Could not get all scaler units from meter: " + getResults);
    }

    final List<String> scalerUnits = this.readScalerUnits(getResults);

    for (int i = 0; i < scalerUnits.size(); i++) {
      captureObjectsWithScalerUnit.put(captureObjects.get(i), scalerUnits.get(i));
    }

    return captureObjectsWithScalerUnit;
  }

  private AttributeAddress[] getScalerUnitAddresses(final List<CaptureObject> captureObjects) {
    return captureObjects.stream().map(this::getScalerUnitAddress).toArray(AttributeAddress[]::new);
  }

  private AttributeAddress getScalerUnitAddress(final CaptureObject captureObject) {
    final CosemObject cosemObject = captureObject.getCosemObject();
    return new AttributeAddress(
        cosemObject.getClassId(),
        new ObisCode(cosemObject.getObis()),
        ExtendedRegisterAttribute.SCALER_UNIT.attributeId());
  }

  private List<String> readScalerUnits(final List<GetResult> getResultList)
      throws ProtocolAdapterException {

    final List<String> scalerUnits = new ArrayList<>();

    for (final GetResult getResult : getResultList) {
      final DataObject scalerUnitObject = getResult.getResultData();

      scalerUnits.add(
          this.dlmsHelper.getScalerUnit(
              scalerUnitObject, "get scaler unit for periodic meter reads"));
    }

    return scalerUnits;
  }

  /**
   * get device model code from a comma seperated list per channel index 1-4 is channel 1-4 and
   * index 0 is device model code of master device
   */
  public String getDeviceModelCodeOfChannel(final String codes, final int channel) {
    if (StringUtils.isNotBlank(codes)) {
      if (channel >= 1 && channel <= 4) {
        final String[] deviceModelCodes = codes.split(",");
        if (deviceModelCodes.length > channel) {
          return deviceModelCodes[channel];
        }
        return "";
      } else {
        throw new IllegalArgumentException("Channel is not a correct value");
      }
    } else {
      return "";
    }
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }
}
