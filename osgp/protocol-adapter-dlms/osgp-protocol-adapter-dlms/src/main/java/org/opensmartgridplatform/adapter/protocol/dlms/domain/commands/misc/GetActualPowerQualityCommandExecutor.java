// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityProfile;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
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

  private final DlmsHelper dlmsHelper;

  private final ObjectConfigService objectConfigService;

  public GetActualPowerQualityCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    super(ActualPowerQualityRequestDto.class);
    this.dlmsHelper = dlmsHelper;
    this.objectConfigService = objectConfigService;
  }

  @Override
  public ActualPowerQualityResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualPowerQualityRequestDto actualPowerQualityRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final PowerQualityProfile profile = this.determineProfile(actualPowerQualityRequestDto);

    final List<CosemObject> pqObjects = this.getPQObjects(device, profile);

    final AttributeAddress[] attributeAddresses =
        this.getAttributeAddresses(pqObjects).toArray(new AttributeAddress[0]);
    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualPowerQuality retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddresses));

    log.info("Retrieving actual power quality");
    final List<GetResult> resultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual power quality", attributeAddresses);

    return this.makeActualPowerQualityResponseDto(resultList, pqObjects);
  }

  private ActualPowerQualityResponseDto makeActualPowerQualityResponseDto(
      final List<GetResult> resultList, final List<CosemObject> pqObjects)
      throws ProtocolAdapterException {
    final ActualPowerQualityResponseDto responseDto = new ActualPowerQualityResponseDto();
    final ActualPowerQualityDataDto actualPowerQualityDataDto =
        this.makeActualPowerQualityDataDto(resultList, pqObjects);
    responseDto.setActualPowerQualityDataDto(actualPowerQualityDataDto);
    return responseDto;
  }

  private ActualPowerQualityDataDto makeActualPowerQualityDataDto(
      final List<GetResult> resultList, final List<CosemObject> pqObjects)
      throws ProtocolAdapterException {

    final List<PowerQualityObjectDto> powerQualityObjects = new ArrayList<>();
    final List<PowerQualityValueDto> powerQualityValues = new ArrayList<>();

    int idx = 0;
    for (final CosemObject pqObject : pqObjects) {
      final PowerQualityObjectDto powerQualityObject;
      final PowerQualityValueDto powerQualityValue;

      final GetResult resultValue = resultList.get(idx++);

      if (pqObject.getClassId() == CLASS_ID_CLOCK) {
        final CosemDateTimeDto cosemDateTime =
            this.dlmsHelper.readDateTime(resultValue, "Actual Power Quality - Time");
        powerQualityValue = new PowerQualityValueDto(cosemDateTime.asDateTime().toDate());
        powerQualityObject = new PowerQualityObjectDto(pqObject.getTag(), null);

      } else if (pqObject.getClassId() == CLASS_ID_REGISTER) {
        final DlmsMeterValueDto meterValue;
        if (this.readScalerValueFromMeter(pqObject)) {
          final GetResult resultValueScalerUnit = resultList.get(idx++);
          meterValue =
              this.dlmsHelper.getScaledMeterValue(
                  resultValue,
                  resultValueScalerUnit,
                  "Actual Power Quality - " + pqObject.getObis());
        } else {
          final String scalerUnit =
              pqObject.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId()).getValue();
          meterValue =
              this.dlmsHelper.getScaledMeterValueWithScalerUnit(
                  resultValue, scalerUnit, "Actual Power Quality - " + pqObject.getObis());
        }

        final BigDecimal value = meterValue != null ? meterValue.getValue() : null;
        final String unit = meterValue != null ? meterValue.getDlmsUnit().getUnit() : null;
        powerQualityValue = new PowerQualityValueDto(value);

        powerQualityObject = new PowerQualityObjectDto(pqObject.getTag(), unit);

      } else if (pqObject.getClassId() == CLASS_ID_DATA) {
        final Integer meterValue =
            this.dlmsHelper.readInteger(
                resultValue, "Actual Power Quality - " + pqObject.getObis());

        powerQualityValue =
            meterValue != null ? new PowerQualityValueDto(new BigDecimal(meterValue)) : null;

        powerQualityObject = new PowerQualityObjectDto(pqObject.getTag(), null);
      } else {
        throw new ProtocolAdapterException(
            String.format(
                "Unsupported ClassId %d for logical name %s",
                pqObject.getClassId(), pqObject.getObis()));
      }
      powerQualityObjects.add(powerQualityObject);
      powerQualityValues.add(powerQualityValue);
    }

    return new ActualPowerQualityDataDto(powerQualityObjects, powerQualityValues);
  }

  private boolean readScalerValueFromMeter(final CosemObject pqObject) {
    if (pqObject.hasAttribute(RegisterAttribute.SCALER_UNIT.attributeId())) {
      final Attribute attribute =
          pqObject.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());
      return attribute.getValuetype() != ValueType.FIXED_IN_PROFILE;
    }
    return false;
  }

  private PowerQualityProfile determineProfile(final ActualPowerQualityRequestDto request) {

    try {
      return PowerQualityProfile.valueOf(request.getProfileType());
    } catch (final IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException(
          "ActualPowerQuality: an unknown profileType was requested: " + request.getProfileType());
    }
  }

  public List<CosemObject> getPQObjects(final DlmsDevice device, final PowerQualityProfile profile)
      throws ProtocolAdapterException {
    final List<CosemObject> allPQObjects = new ArrayList<>();

    try {
      // Get clock object (should be the first in the list)
      final CosemObject clockObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(), device.getProtocolVersion(), DlmsObjectType.CLOCK);
      allPQObjects.add(clockObject);

      // Create map with the required properties and values for the power quality objects
      final EnumMap<ObjectProperty, List<String>> pqProperties =
          new EnumMap<>(ObjectProperty.class);
      pqProperties.put(ObjectProperty.PQ_PROFILE, Collections.singletonList(profile.name()));
      pqProperties.put(
          ObjectProperty.PQ_REQUEST,
          List.of(
              device.isPolyphase()
                  ? PowerQualityRequest.ACTUAL_PP.name()
                  : PowerQualityRequest.ACTUAL_SP.name()));

      // Get matching power quality objects from config
      final List<CosemObject> pqObjects =
          this.objectConfigService.getCosemObjectsWithProperties(
              device.getProtocolName(), device.getProtocolVersion(), pqProperties);

      allPQObjects.addAll(pqObjects);
      return allPQObjects;
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(this.ERROR_IN_OBJECT_CONFIG, e);
    }
  }

  private List<AttributeAddress> getAttributeAddresses(final List<CosemObject> pqObjects) {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();
    for (final CosemObject pqObject : pqObjects) {
      attributeAddresses.add(this.getAttributeAddress(pqObject));
      if (this.readScalerValueFromMeter(pqObject)) {
        attributeAddresses.add(
            new AttributeAddress(
                pqObject.getClassId(),
                pqObject.getObis(),
                RegisterAttribute.SCALER_UNIT.attributeId()));
      }
    }
    return attributeAddresses.stream().filter(Objects::nonNull).toList();
  }

  private AttributeAddress getAttributeAddress(final CosemObject object) {
    final String obis = object.getObis();

    if (object.getClassId() == CLASS_ID_CLOCK) {
      return new AttributeAddress(CLASS_ID_CLOCK, obis, ClockAttribute.TIME.attributeId());
    } else if (object.getClassId() == CLASS_ID_DATA) {
      return new AttributeAddress(CLASS_ID_DATA, obis, DataAttribute.VALUE.attributeId());
    } else if (object.getClassId() == CLASS_ID_REGISTER) {
      return new AttributeAddress(CLASS_ID_REGISTER, obis, RegisterAttribute.VALUE.attributeId());
    } else {
      log.warn("No attribute addresses returned for interface class of {}", object.getTag());
      return null;
    }
  }
}
