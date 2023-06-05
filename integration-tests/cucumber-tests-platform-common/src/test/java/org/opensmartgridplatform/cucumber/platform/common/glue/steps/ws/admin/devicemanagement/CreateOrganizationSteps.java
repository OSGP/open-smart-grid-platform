// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.Organisation;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the create organization requests steps */
public class CreateOrganizationSteps {

  @Autowired private AdminDeviceManagementClient client;

  /**
   * @throws Throwable
   */
  @When("^receiving a create organization request$")
  public void receivingACreateOrganizationRequest(final Map<String, String> requestSettings)
      throws Throwable {

    final CreateOrganisationRequest request = new CreateOrganisationRequest();
    final Organisation organization = new Organisation();

    // Required fields
    organization.setName(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_NAME,
            PlatformCommonDefaults.DEFAULT_ORGANIZATION_NAME));
    organization.setOrganisationIdentification(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    organization.setPrefix(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_PREFIX,
            PlatformCommonDefaults.DEFAULT_ORGANIZATION_PREFIX));

    final PlatformFunctionGroup platformFunctionGroup =
        getEnum(
            requestSettings,
            PlatformCommonKeys.KEY_PLATFORM_FUNCTION_GROUP,
            PlatformFunctionGroup.class,
            PlatformCommonDefaults.DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP);
    organization.setFunctionGroup(platformFunctionGroup);

    for (final String domain :
        getString(
                requestSettings,
                PlatformCommonKeys.KEY_DOMAINS,
                PlatformCommonDefaults.DEFAULT_DOMAINS)
            .split(";")) {
      organization.getDomains().add(Enum.valueOf(PlatformDomain.class, domain));
    }

    // Optional fields
    organization.setEnabled(
        getBoolean(
            requestSettings,
            PlatformCommonKeys.KEY_ENABLED,
            PlatformDefaults.DEFAULT_ORGANIZATION_ENABLED));

    request.setOrganisation(organization);

    try {
      ScenarioContext.current()
          .put(PlatformCommonKeys.RESPONSE, this.client.createOrganization(request));
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
    }
  }

  /**
   * @throws Throwable
   */
  @When("^receiving a create organization request as an unauthorized organization$")
  public void receivingACreateOrganizationRequestAsAnUnauthorizedOrganization(
      final Map<String, String> requestSettings) throws Throwable {

    // Force WSTF to use a different organization to send the requests with.
    // (Cerificate is used from the certificates directory).
    ScenarioContext.current()
        .put(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingACreateOrganizationRequest(requestSettings);
  }

  /**
   * Verify that the create organization response is successful.
   *
   * @throws Throwable
   */
  @Then("^the create organization response is successful$")
  public void theCreateOrganizationResponseIsSuccessful() throws Throwable {
    assertThat(
            ScenarioContext.current().get(PlatformCommonKeys.RESPONSE)
                instanceof CreateOrganisationResponse)
        .isTrue();
  }

  /**
   * Verify that the create organization response contains the fault with the given expectedResult
   * parameters.
   *
   * @throws Throwable
   */
  @Then("^the create organization response contains soap fault$")
  public void theCreateOrganizationResponseContainsSoapFault(
      final Map<String, String> expectedResult) throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
