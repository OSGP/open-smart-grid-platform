// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ADMINISTRATIVE_IN_OUT;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class GetAdministrativeStatusCommandExecutor
    extends AbstractCommandExecutor<Void, AdministrativeStatusTypeDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetAdministrativeStatusCommandExecutor.class);

  private final ConfigurationMapper configurationMapper;

  private final ObjectConfigService objectConfigService;

  public GetAdministrativeStatusCommandExecutor(
      final ObjectConfigService objectConfigService,
      final ConfigurationMapper configurationMapper) {
    super(GetAdministrativeStatusDataDto.class);

    this.objectConfigService = objectConfigService;
    this.configurationMapper = configurationMapper;
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return null;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AdministrativeStatusTypeDto executionResult)
      throws ProtocolAdapterException {

    return new AdministrativeStatusTypeResponseDto(executionResult);
  }

  @Override
  public AdministrativeStatusTypeDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void useless,
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
    final AttributeAddress getParameter =
        new AttributeAddress(
            administrativeInOutObject.getClassId(),
            administrativeInOutObject.getObis(),
            DataAttribute.VALUE.attributeId());

    conn.getDlmsMessageListener()
        .setDescription(
            "GetAdministrativeStatus, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(getParameter));

    LOGGER.debug(
        "Retrieving current administrative status by issuing get request for class id: {}, obis code: {}, attribute id: {}",
        administrativeInOutObject.getClassId(),
        administrativeInOutObject.getObis(),
        DataAttribute.VALUE.attributeId());

    final DataObject dataObject = this.getValidatedResultData(conn, getParameter);

    return this.configurationMapper.map(dataObject.getValue(), AdministrativeStatusTypeDto.class);
  }
}
