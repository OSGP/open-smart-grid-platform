// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ADMINISTRATIVE_IN_OUT;

import java.io.IOException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetAdministrativeStatusCommandExecutor
    extends AbstractCommandExecutor<AdministrativeStatusTypeDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetAdministrativeStatusCommandExecutor.class);

  private final ConfigurationMapper configurationMapper;

  private final ObjectConfigService objectConfigService;

  public SetAdministrativeStatusCommandExecutor(
      final ObjectConfigService objectConfigService,
      final ConfigurationMapper configurationMapper) {
    super(AdministrativeStatusTypeDataDto.class);

    this.objectConfigService = objectConfigService;
    this.configurationMapper = configurationMapper;
  }

  @Override
  public AdministrativeStatusTypeDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final AdministrativeStatusTypeDataDto administrativeStatusTypeDataDto =
        (AdministrativeStatusTypeDataDto) bundleInput;

    return administrativeStatusTypeDataDto.getAdministrativeStatusType();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Set administrative status was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AdministrativeStatusTypeDto administrativeStatusType,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final CosemObject administrativeInOutObject;
    try {
      administrativeInOutObject =
          this.objectConfigService.getCosemObject(
              device.getProtocolName(), device.getProtocolVersion(), ADMINISTRATIVE_IN_OUT);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException(AbstractCommandExecutor.ERROR_IN_OBJECT_CONFIG, e);
    }

    LOGGER.debug(
        "Set administrative status by issuing set request for class id: {}, obis code: {}, attribute id: {}",
        administrativeInOutObject.getClassId(),
        administrativeInOutObject.getObis(),
        DataAttribute.VALUE.attributeId());

    final AttributeAddress attributeAddress =
        new AttributeAddress(
            administrativeInOutObject.getClassId(),
            administrativeInOutObject.getObis(),
            DataAttribute.VALUE.attributeId());
    final DataObject value =
        DataObject.newEnumerateData(
            this.configurationMapper.map(administrativeStatusType, Integer.class));

    final SetParameter setParameter = new SetParameter(attributeAddress, value);

    conn.getDlmsMessageListener()
        .setDescription(
            "SetAdminstrativeStatus to "
                + administrativeStatusType
                + ", set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
