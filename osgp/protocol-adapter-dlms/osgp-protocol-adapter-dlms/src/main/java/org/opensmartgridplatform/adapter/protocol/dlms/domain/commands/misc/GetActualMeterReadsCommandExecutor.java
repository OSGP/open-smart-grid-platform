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

import java.util.ArrayList;
import java.util.EnumMap;
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
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
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

  private static final byte ATTRIBUTE_ID_VALUE = 2;

  private static final List<DlmsObjectType> OBJECTTYPES_FOR_ACTUALS =
      List.of(
          CLOCK,
          ACTIVE_ENERGY_IMPORT,
          ACTIVE_ENERGY_IMPORT_RATE_1,
          ACTIVE_ENERGY_IMPORT_RATE_2,
          ACTIVE_ENERGY_EXPORT,
          ACTIVE_ENERGY_EXPORT_RATE_1,
          ACTIVE_ENERGY_EXPORT_RATE_2);

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

    final List<CosemObject> cosemObjects = this.getCosemObjectsFromConfig(device);

    final AttributeAddress[] atttributeAddresses = this.getAddressesForAllObjects(cosemObjects);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetActualMeterReads retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(atttributeAddresses));

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(
            conn, device, "retrieve actual meter reads", atttributeAddresses);

    final Map<DlmsObjectType, DlmsMeterValueDto> valueMap = new EnumMap<>(DlmsObjectType.class);
    DateTime time = null;
    int index = 0;
    for (final CosemObject object : cosemObjects) {
      if (object.getClassId() == InterfaceClass.CLOCK.id()) {
        time = this.getTime(getResultList, index++);
      } else if (object instanceof final Register register) {
        index = this.getValue(register, getResultList, valueMap, index);
      }
    }

    if (time == null) {
      throw new ProtocolAdapterException(
          "Unexpected null/unspecified value for Actual Energy Reads Time");
    }

    return new MeterReadsResponseDto(
        time.toDate(),
        new ActiveEnergyValuesDto(
            valueMap.get(ACTIVE_ENERGY_IMPORT),
            valueMap.get(ACTIVE_ENERGY_EXPORT),
            valueMap.get(ACTIVE_ENERGY_IMPORT_RATE_1),
            valueMap.get(ACTIVE_ENERGY_IMPORT_RATE_2),
            valueMap.get(ACTIVE_ENERGY_EXPORT_RATE_1),
            valueMap.get(ACTIVE_ENERGY_EXPORT_RATE_2)));
  }

  private int getValue(
      final Register register,
      final List<GetResult> getResultList,
      final Map<DlmsObjectType, DlmsMeterValueDto> valueMap,
      int index)
      throws ProtocolAdapterException {
    final DataObject valueObject = getResultList.get(index++).getResultData();
    final String unit;
    if (register.needsScalerUnitFromMeter()) {
      unit =
          this.dlmsHelper.getScalerUnit(
              getResultList.get(index++).getResultData(), "Actual Energy Reads scaler_unit");
    } else {
      unit = register.getScalerUnit();
    }
    final DlmsMeterValueDto value =
        this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            valueObject, unit, "Actual Energy Reads " + register.getTag());

    valueMap.put(DlmsObjectType.valueOf(register.getTag()), value);

    return index;
  }

  private DateTime getTime(final List<GetResult> getResultList, final int index)
      throws ProtocolAdapterException {
    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.readDateTime(getResultList.get(index), "Actual Energy Reads Time");
    return cosemDateTime.asDateTime();
  }

  private List<CosemObject> getCosemObjectsFromConfig(final DlmsDevice device)
      throws ProtocolAdapterException {
    final List<CosemObject> cosemObjects;
    try {
      cosemObjects =
          this.objectConfigService.getCosemObjects(
              device.getProtocolName(), device.getProtocolVersion(), OBJECTTYPES_FOR_ACTUALS);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }
    return cosemObjects;
  }

  private AttributeAddress[] getAddressesForAllObjects(final List<CosemObject> objects) {
    return objects.stream()
        .map(this::getAddresses)
        .flatMap(List::stream)
        .toArray(AttributeAddress[]::new);
  }

  private List<AttributeAddress> getAddresses(final CosemObject object) {
    final List<AttributeAddress> addresses = new ArrayList<>();
    addresses.add(new AttributeAddress(object.getClassId(), object.getObis(), ATTRIBUTE_ID_VALUE));

    if (object instanceof final Register register && register.needsScalerUnitFromMeter()) {
      addresses.add(
          new AttributeAddress(
              object.getClassId(), object.getObis(), RegisterAttribute.SCALER_UNIT.attributeId()));
    }

    return addresses;
  }
}
