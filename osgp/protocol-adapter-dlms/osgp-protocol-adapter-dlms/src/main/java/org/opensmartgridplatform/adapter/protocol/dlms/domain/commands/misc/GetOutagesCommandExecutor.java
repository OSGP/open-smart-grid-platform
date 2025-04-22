// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_FAILURE_EVENT_LOG;

import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.DataObjectToOutageListConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OutageDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetOutagesCommandExecutor
    extends AbstractCommandExecutor<GetOutagesRequestDto, List<OutageDto>> {

  private final DataObjectToOutageListConverter dataObjectToOutageListConverter;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public GetOutagesCommandExecutor(
      final DataObjectToOutageListConverter dataObjectToOutageListConverter,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(GetOutagesRequestDto.class);
    this.dataObjectToOutageListConverter = dataObjectToOutageListConverter;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final List<OutageDto> executionResult)
      throws ProtocolAdapterException {
    return new GetOutagesResponseDto(executionResult);
  }

  @Override
  public List<OutageDto> execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetOutagesRequestDto getOutagesRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AttributeAddress eventLogBuffer =
        this.objectConfigServiceHelper.findDefaultAttributeAddress(
            device, Protocol.forDevice(device), POWER_FAILURE_EVENT_LOG);

    conn.getDlmsMessageListener()
        .setDescription(
            "RetrieveOutages, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(eventLogBuffer));

    final GetResult getResult;
    try {
      getResult = conn.getConnection().get(eventLogBuffer);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    if (getResult == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving event register POWER_FAILURE_EVENT_LOG");
    }

    if (!AccessResultCode.SUCCESS.equals(getResult.getResultCode())) {
      log.info(
          "Result of getting events for POWER_FAILURE_EVENT_LOG is {}", getResult.getResultCode());
      throw new ProtocolAdapterException(
          "Getting the outages from POWER_FAILURE_EVENT_LOG from the meter resulted in: "
              + getResult.getResultCode());
    }

    final DataObject resultData = getResult.getResultData();
    return this.dataObjectToOutageListConverter.convert(resultData);
  }
}
