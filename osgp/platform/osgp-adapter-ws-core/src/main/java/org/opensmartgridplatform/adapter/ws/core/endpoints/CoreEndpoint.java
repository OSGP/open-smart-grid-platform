/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.endpoints;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpoint.WebserviceEndpoint;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseUrlService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.exceptionhandling.UnknownCorrelationUidException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

abstract class CoreEndpoint implements WebserviceEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(CoreEndpoint.class);

  @Autowired protected ResponseUrlService responseUrlService;

  @Autowired protected ResponseDataService responseDataService;

  /**
   * Rethrow exception if it already is a functional or technical exception, otherwise throw new
   * technical exception.
   *
   * @param e cause
   * @throws OsgpException
   */
  @Override
  public void handleException(final Exception e) throws OsgpException {
    if (e instanceof OsgpException) {
      if (e instanceof UnknownCorrelationUidException) {
        LOGGER.warn(e.getMessage());
      } else {
        LOGGER.error("Exception occurred: ", e);
      }
      throw (OsgpException) e;
    } else {
      LOGGER.error("Exception occurred: ", e);
      throw new TechnicalException(ComponentType.WS_CORE, e);
    }
  }

  protected ResponseMessage getResponseMessage(final AsyncRequest asyncRequest) {
    try {
      return (ResponseMessage)
          this.responseDataService
              .dequeue(asyncRequest.getCorrelationUid(), ComponentType.WS_CORE)
              .getMessageData();
    } catch (final UnknownCorrelationUidException unknownCorrelationUidException) {
      LOGGER.info(
          "No message with correlationUID: {} has been found, NOT_FOUND will be returned.",
          asyncRequest.getCorrelationUid());
      return this.createEmptyMessage(asyncRequest.getCorrelationUid());
    }
  }

  protected void throwExceptionIfResultNotOk(
      final ResponseData meterResponseData, final String exceptionContext) throws OsgpException {
    if (OsgpResultType.NOT_OK
        == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
      if (meterResponseData.getMessageData() instanceof String) {
        throw new TechnicalException(
            ComponentType.WS_CORE, (String) meterResponseData.getMessageData(), null);
      } else if (meterResponseData.getMessageData() instanceof OsgpException) {
        throw (OsgpException) meterResponseData.getMessageData();
      } else {
        throw new TechnicalException(
            ComponentType.WS_CORE,
            String.format("An exception occurred %s.", exceptionContext),
            null);
      }
    }
  }

  @Override
  public void saveResponseUrlIfNeeded(final String correlationUid, final String responseUrl) {
    this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
  }

  private ResponseMessage createEmptyMessage(final String correlationUid) {
    return ResponseMessage.newResponseMessageBuilder()
        .withCorrelationUid(correlationUid)
        .withResult(ResponseMessageResultType.NOT_FOUND)
        .build();
  }
}
