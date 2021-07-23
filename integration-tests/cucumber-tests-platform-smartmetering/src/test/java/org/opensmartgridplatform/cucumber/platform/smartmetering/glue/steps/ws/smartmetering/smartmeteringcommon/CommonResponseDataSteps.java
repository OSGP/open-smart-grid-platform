/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringcommon;

import static org.junit.Assert.assertThrows;

import io.cucumber.java.en.When;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.smcommon.DeleteResponseDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.smcommon.DeleteResponseDataResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.common.SmartMeteringCommonClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class CommonResponseDataSteps {

  @Autowired private SmartMeteringCommonClient client;

  @When("^the delete response data request with correlation uid \"([^\"]*)\"$")
  public void theDeleteResponseDataRequestWithCorrelationUid(final String correlationUid)
      throws WebServiceSecurityException {

    final DeleteResponseDataRequest request = new DeleteResponseDataRequest();
    request.setCorrelationUid(correlationUid);

    final DeleteResponseDataResponse response = this.client.sendDeleteResponseDataRequest(request);

    assertThat(response).isNotNull();
  }

  @When(
      "^the delete response data request with correlation uid \"([^\"]*)\" should throw SoapFault$")
  public void theDeleteResponseDataRequestWithCorrelationUidShouldThrowSoapFault(
      final String correlationUid) throws WebServiceSecurityException {

    final DeleteResponseDataRequest request = new DeleteResponseDataRequest();
    request.setCorrelationUid(correlationUid);

    final SoapFaultClientException exception =
        assertThrows(
            SoapFaultClientException.class,
            () -> {
              this.client.sendDeleteResponseDataRequest(request);
            });
  }
}
