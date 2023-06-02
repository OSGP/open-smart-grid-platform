//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.smcommon.DeleteResponseDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.smcommon.DeleteResponseDataResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class SmartMeteringCommonEndpoint extends SmartMeteringEndpoint {

  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-common/2014/10";

  @PayloadRoot(localPart = "DeleteResponseDataRequest", namespace = NAMESPACE)
  @ResponsePayload
  public DeleteResponseDataResponse deleteResponseData(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final DeleteResponseDataRequest request)
      throws OsgpException {

    log.info(
        "Incoming call to delete ResponseData [correlationId={} | organisation={}]",
        request.getCorrelationUid(),
        organisationIdentification);

    this.responseDataService.delete(request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

    // Create response.
    final DeleteResponseDataResponse response = new DeleteResponseDataResponse();
    response.setResult(OsgpResultType.OK);
    response.setResultString("response data has been deleted");
    return response;
  }
}
