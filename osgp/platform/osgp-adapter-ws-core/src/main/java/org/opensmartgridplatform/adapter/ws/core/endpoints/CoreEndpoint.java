// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.endpoints;

import org.opensmartgridplatform.adapter.ws.endpoint.WebserviceEndpoint;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
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
      return createEmptyMessage(asyncRequest.getCorrelationUid());
    }
  }

  protected static void throwExceptionIfResultNotOk(
      final ResponseMessage responseMessage, final String exceptionContext) throws OsgpException {
    if (ResponseMessageResultType.NOT_OK == responseMessage.getResult()
        && responseMessage.getOsgpException() != null) {
      LOGGER.error(
          "Unexpected exception while {}: {}",
          exceptionContext,
          getExceptionMessage(responseMessage.getOsgpException()));
      throw responseMessage.getOsgpException();
    }
  }

  private static String getExceptionMessage(final OsgpException exception) {
    if (exception.getCause() != null) {
      return exception.getCause().getMessage();
    }
    return exception.getMessage();
  }

  @Override
  public void saveResponseUrlIfNeeded(final String correlationUid, final String responseUrl) {
    this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
  }

  private static ResponseMessage createEmptyMessage(final String correlationUid) {
    return ResponseMessage.newResponseMessageBuilder()
        .withCorrelationUid(correlationUid)
        .withResult(ResponseMessageResultType.NOT_FOUND)
        .build();
  }
}
