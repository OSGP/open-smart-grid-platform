/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * this class holds functionality to implement the message handling of a request to decouple a
 * device and a m-bus device
 */
@Component
public class DecoupleMbusDeviceRequestMessageProcessor extends BaseRequestMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringInstallationService")
  private InstallationService installationService;

  @Autowired
  protected DecoupleMbusDeviceRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.DECOUPLE_MBUS_DEVICE);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.
   * AbstractRequestMessageProcessor#handleMessage(org.opensmartgridplatform.shared.
   * infra.jms.DeviceMessageMetadata, java.lang.Object)
   */
  @Override
  protected void handleMessage(
      final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {
    final DecoupleMbusDeviceRequestData requestData = (DecoupleMbusDeviceRequestData) dataObject;
    this.installationService.decoupleMbusDevice(deviceMessageMetadata, requestData);
  }
}
