// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetSpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ValueToSetDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SetSpecificAttributeValueCommandExecutor
    extends AbstractCommandExecutor<SetSpecificAttributeValueRequestDto, Void> {

  private final ObjectConfigService objectConfigService;
  private final DlmsHelper dlmsHelper;

  public SetSpecificAttributeValueCommandExecutor(
      final ObjectConfigService objectConfigService, final DlmsHelper dlmsHelper) {
    super(SetSpecificAttributeValueRequestDto.class);
    this.objectConfigService = objectConfigService;
    this.dlmsHelper = dlmsHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final Void executionResult)
      throws ProtocolAdapterException {
    return new ActionResponseDto("Set specific attribute was successful");
  }

  @Override
  public Void execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetSpecificAttributeValueRequestDto requestData,
      final MessageMetadata messageMetadata)
      throws FunctionalException, ProtocolAdapterException {

    final List<SetParameter> setParameters =
        this.getSetParameters(device, requestData.getValuesToSet());

    conn.getDlmsMessageListener()
        .setDescription(
            "Setting value(s) in "
                + JdlmsObjectToStringUtil.describeAttributes(
                    setParameters.stream()
                        .map(SetParameter::getAttributeAddress)
                        .toArray(AttributeAddress[]::new)));

    final List<AccessResultCode> resultCodes =
        this.dlmsHelper.setWithList(conn, device, setParameters);

    if (resultCodes.isEmpty()) {
      throw new ProtocolAdapterException("No resultCodes received while setting values");
    }

    if (!resultCodes.stream().allMatch(code -> code.equals(AccessResultCode.SUCCESS))) {
      log.debug("Result of set specific value is {}", resultCodes);
      throw new ProtocolAdapterException("Set specific value resulted in: " + resultCodes);
    }

    return null;
  }

  private List<SetParameter> getSetParameters(
      final DlmsDevice device, final List<ValueToSetDto> valueToSetDtos)
      throws ProtocolAdapterException {
    final List<SetParameter> setParameters = new ArrayList<>();
    for (final ValueToSetDto valueToSetDto : valueToSetDtos) {
      setParameters.add(this.getSetParameter(device, valueToSetDto));
    }
    return setParameters;
  }

  private SetParameter getSetParameter(final DlmsDevice device, final ValueToSetDto valueToSetDto)
      throws ProtocolAdapterException {
    final DlmsObjectType objectType = DlmsObjectType.valueOf(valueToSetDto.getObjectType());
    final CosemObject cosemObject;
    try {
      cosemObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(), device.getProtocolVersion(), objectType);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }

    log.debug(
        "Set specific attribute value, class id: {}, obis code: {}, attribute id: {}, value: {}",
        cosemObject.getClassId(),
        cosemObject.getObis(),
        valueToSetDto.getAttribute(),
        valueToSetDto.getIntValue());

    final Attribute attribute = cosemObject.getAttribute(valueToSetDto.getAttribute());

    final DlmsDataType dataType = attribute.getDatatype();

    final DataObject data =
        switch (dataType) {
          case UNSIGNED -> DataObject.newUInteger8Data(valueToSetDto.getIntValue().shortValue());
          case LONG_UNSIGNED -> DataObject.newUInteger16Data(valueToSetDto.getIntValue());
          case DOUBLE_LONG_UNSIGNED -> DataObject.newUInteger32Data(valueToSetDto.getIntValue());
          case LONG64_UNSIGNED -> DataObject.newUInteger64Data(valueToSetDto.getIntValue());
          case INTEGER -> DataObject.newInteger8Data(valueToSetDto.getIntValue().byteValue());
          case LONG -> DataObject.newInteger16Data(valueToSetDto.getIntValue().shortValue());
          case DOUBLE_LONG -> DataObject.newInteger32Data(valueToSetDto.getIntValue());
          case LONG64 -> DataObject.newInteger64Data(valueToSetDto.getIntValue());
          default ->
              throw new ProtocolAdapterException(
                  "Datatype " + dataType.name() + " not supported for integer value");
        };

    final AttributeAddress attributeAddress =
        new AttributeAddress(
            cosemObject.getClassId(),
            new ObisCode(cosemObject.getObis()),
            valueToSetDto.getAttribute());

    return new SetParameter(attributeAddress, data);
  }
}
