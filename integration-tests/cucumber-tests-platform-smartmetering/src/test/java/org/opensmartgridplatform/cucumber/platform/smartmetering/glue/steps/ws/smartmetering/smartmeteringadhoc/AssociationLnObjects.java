// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.AssociationLnListElement;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.AssociationLnObjectsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class AssociationLnObjects {

  @Autowired
  SmartMeteringAdHocRequestClient<
          GetAssociationLnObjectsAsyncResponse, GetAssociationLnObjectsRequest>
      requestClient;

  @Autowired
  SmartMeteringAdHocResponseClient<
          GetAssociationLnObjectsResponse, GetAssociationLnObjectsAsyncRequest>
      responseClient;

  @When("^receiving a retrieve association LN objectlist request$")
  public void receivingARetrieveAssociationLNObjectlistRequest(final Map<String, String> settings)
      throws Throwable {

    final GetAssociationLnObjectsRequest request =
        AssociationLnObjectsRequestFactory.fromParameterMap(settings);
    final GetAssociationLnObjectsAsyncResponse asyncResponse =
        this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncRespone should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the objectlist should be returned$")
  public void theObjectlistShouldBeReturned(final Map<String, String> settings) throws Throwable {

    final GetAssociationLnObjectsAsyncRequest asyncRequest =
        AssociationLnObjectsRequestFactory.fromScenarioContext();
    final GetAssociationLnObjectsResponse response = this.responseClient.getResponse(asyncRequest);

    assertThat(response.getResult()).as("Response should be OK").isEqualTo(OsgpResultType.OK);

    assertThat(response.getAssociationLnList())
        .as("Response should contain an AssociationLnList")
        .isNotNull();

    assertThat(response.getAssociationLnList().getAssociationLnListElement().get(0))
        .as("AssociationLnList should have at least one entry")
        .isNotNull();

    final AssociationLnListElement element =
        response.getAssociationLnList().getAssociationLnListElement().get(0);
    assertThat(element.getAccessRights()).as("AccessRights should be present").isNotNull();

    assertThat(element.getLogicalName()).as("LogicalName should be present").isNotNull();

    assertThat(element.getVersion()).as("Version should be present").isNotNull();

    assertThat(
            element
                .getAccessRights()
                .getAttributeAccess()
                .getAttributeAccessItem()
                .get(0)
                .getAttributeId())
        .as("AttributeId should be present")
        .isNotNull();
  }
}
