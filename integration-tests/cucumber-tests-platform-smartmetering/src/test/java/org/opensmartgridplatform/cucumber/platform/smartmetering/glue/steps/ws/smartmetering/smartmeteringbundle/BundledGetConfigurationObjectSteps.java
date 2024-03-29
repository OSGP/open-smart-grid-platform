// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;

public class BundledGetConfigurationObjectSteps extends BaseBundleSteps {

  static final String GPRS_OPERATION_MODE = "GprsOperationMode";

  @Given("^the bundle request contains a get configuration object action$")
  public void theBundleRequestContainsAGetConfigurationObject() throws Throwable {

    final GetConfigurationObjectRequest action = new GetConfigurationObjectRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get configuration object response$")
  public void theBundleResponseShouldContainAConfigurationObjectResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof GetConfigurationObjectResponse)
        .as("response should be a GetConfigurationResponse object")
        .isTrue();
  }

  @Then("^the bundle response should contain a get configuration object response with values$")
  public void theBundleResponseShouldContainAConfigurationObjectResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof GetConfigurationObjectResponse)
        .as("response should be a GetConfigurationResponse object")
        .isTrue();

    final ConfigurationObject configurationObject =
        ((GetConfigurationObjectResponse) response).getConfigurationObject();

    if (values.containsKey(GPRS_OPERATION_MODE)
        && StringUtils.isNotBlank(values.get(GPRS_OPERATION_MODE))) {
      assertThat(configurationObject.getGprsOperationMode())
          .as("The gprs operation mode is not equal")
          .hasToString(values.get(GPRS_OPERATION_MODE));
    } else {
      assertThat(configurationObject.getGprsOperationMode()).isNull();
    }

    configurationObject
        .getConfigurationFlags()
        .getConfigurationFlag()
        .forEach(f -> this.testConfigurationFlag(f, values));
  }

  private void testConfigurationFlag(
      final ConfigurationFlag configFlag, final Map<String, String> settings) {
    final String key = configFlag.getConfigurationFlagType().name();
    final boolean value = getBoolean(settings, key);
    assertThat(configFlag.isEnabled())
        .as("The enabled value for configuration flag " + key)
        .isEqualTo(value);
  }
}
