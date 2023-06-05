// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceAdministrativeRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * this class holds functionality to implement the message handling of a request to administratively
 * decouple a device and an m-bus device
 */
@Component
public class DecoupleMbusDeviceAdministrativeRequestMessageProcessor
    extends BaseRequestMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringInstallationService")
  private InstallationService installationService;

  @Autowired
  protected DecoupleMbusDeviceAdministrativeRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.DECOUPLE_MBUS_DEVICE_ADMINISTRATIVE);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.
   * AbstractRequestMessageProcessor#handleMessage(org.opensmartgridplatform.shared.
   * infra.jms.MessageMetadata, java.lang.Object)
   */
  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {
    final DecoupleMbusDeviceAdministrativeRequestData requestData =
        (DecoupleMbusDeviceAdministrativeRequestData) dataObject;
    this.installationService.decoupleMbusDeviceAdministrative(deviceMessageMetadata, requestData);
  }
}
