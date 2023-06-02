//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetKeysRequestFactory.getSecretTypesFromParameterMap;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationKeyConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SecretType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.WsSmartMeteringApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetKeysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class GetKeys {

  private static final String OPERATION = "Get keys";

  @Autowired
  @Qualifier("wsSmartMeteringNotificationApplicationName")
  private String applicationName;

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private WsSmartMeteringApplicationKeyConfigurationRepository
      applicationKeyConfigurationRepository;

  @When("^a get keys request is received$")
  public void aGetKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {

    final GetKeysRequest request = GetKeysRequestFactory.fromParameterMap(settings);

    final GetKeysAsyncResponse asyncResponse =
        this.smartMeteringConfigurationClient.getKeys(request);

    assertThat(asyncResponse).as("getKeysAsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Given("^an application key is configured$")
  public void anApplicationKeyIsConfigured(final Map<String, String> settings) throws Throwable {
    final ApplicationDataLookupKey applicationDataLookupKey =
        new ApplicationDataLookupKey(
            settings.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION), this.applicationName);
    final ApplicationKeyConfiguration applicationKeyConfiguration =
        new ApplicationKeyConfiguration(
            applicationDataLookupKey,
            "/etc/osp/smartmetering/keys/application/smartmetering-rsa-public.key");
    this.applicationKeyConfigurationRepository.save(applicationKeyConfiguration);
  }

  @Then("^the get keys response should return the requested keys$")
  public void theGetKeysResponseIsReturned(final Map<String, String> expectedValues)
      throws Throwable {

    final GetKeysAsyncRequest asyncRequest = GetKeysRequestFactory.fromScenarioContext();
    final GetKeysResponse response =
        this.smartMeteringConfigurationClient.retrieveGetKeysResponse(asyncRequest);

    assertThat(response).isNotNull();

    assertThat(response.getResult())
        .as(OPERATION + ", Checking result:")
        .isEqualTo(OsgpResultType.OK);

    final List<GetKeysResponseData> responseDataList = response.getGetKeysResponseData();

    assertThat(responseDataList)
        .noneMatch(getKeysResponseData -> ArrayUtils.isEmpty(getKeysResponseData.getSecretValue()));

    final List<SecretType> secretTypesInResponse =
        responseDataList.stream()
            .map(GetKeysResponseData::getSecretType)
            .collect(Collectors.toList());

    final List<SecretType> expectedSecretTypes = getSecretTypesFromParameterMap(expectedValues);

    assertThat(secretTypesInResponse).containsExactlyInAnyOrderElementsOf(expectedSecretTypes);
  }
}
