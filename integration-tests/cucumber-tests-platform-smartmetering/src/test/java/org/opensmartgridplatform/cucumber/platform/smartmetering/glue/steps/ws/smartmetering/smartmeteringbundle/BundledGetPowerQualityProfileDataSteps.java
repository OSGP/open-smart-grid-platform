// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityProfileData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.builders.GetPowerQualityProfileRequestBuilder;

@Slf4j
public class BundledGetPowerQualityProfileDataSteps extends BaseBundleSteps {

  public static final String CLASS_ID = "classId";

  public static final String LOGICAL_NAME = "logicalName";

  public static final String ATTRIBUTE_INDEX = "attributeIndex";

  public static final String DATA_INDEX = "dataIndex";

  public static final String UNIT = "unit";

  @Given("^the bundle request contains a get power quality profile request with parameters$")
  public void theBundleRequestContainsAGetPowerQualityProfileRequestAction(
      final Map<String, String> parameters) throws Throwable {

    final GetPowerQualityProfileRequest action =
        new GetPowerQualityProfileRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a power quality profile response with (\\d++) values$")
  public void theBundleResponseShouldContainAGetPowerQualityProfileResponse(
      final int nrOfValues, final DataTable valuesDataTable) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(GetPowerQualityProfileResponse.class);

    final GetPowerQualityProfileResponse getPowerQualityProfileResponse =
        (GetPowerQualityProfileResponse) response;
    final PowerQualityProfileData powerQualityProfileData =
        getPowerQualityProfileResponse.getPowerQualityProfileDatas().get(0);

    this.logData(powerQualityProfileData);

    final List<Map<String, String>> expectedCaptureObjects =
        valuesDataTable.asMaps(String.class, String.class);

    for (final CaptureObject captureObject :
        powerQualityProfileData.getCaptureObjectList().getCaptureObjects()) {
      log.info(
          "Search data in response | {} | {} | {} | {} | {} |",
          captureObject.getClassId(),
          captureObject.getLogicalName(),
          captureObject.getAttributeIndex(),
          captureObject.getDataIndex(),
          captureObject.getUnit());

      this.assertCaptureObjectIsExpected(captureObject, expectedCaptureObjects);
    }
    assertThat(powerQualityProfileData.getCaptureObjectList().getCaptureObjects())
        .hasSize(expectedCaptureObjects.size());

    assertThat(powerQualityProfileData.getProfileEntryList().getProfileEntries())
        .hasSize(nrOfValues);
  }

  private void assertCaptureObjectIsExpected(
      final CaptureObject captureObject, final List<Map<String, String>> expectedCaptureObjects) {
    final Optional<Map<String, String>> optExpectedCaptureObject =
        expectedCaptureObjects.stream()
            .filter(
                d ->
                    d.get(LOGICAL_NAME).equals(captureObject.getLogicalName())
                        && new BigInteger(d.get(ATTRIBUTE_INDEX))
                            .equals(captureObject.getAttributeIndex()))
            .findFirst();
    assertThat(optExpectedCaptureObject).isPresent();
    final Map<String, String> expectedCaptureObject = optExpectedCaptureObject.get();

    assertThat(captureObject.getClassId())
        .isEqualTo(Long.valueOf(expectedCaptureObject.get(CLASS_ID)));
    assertThat(captureObject.getLogicalName()).isEqualTo(expectedCaptureObject.get(LOGICAL_NAME));
    assertThat(captureObject.getAttributeIndex())
        .isEqualTo(new BigInteger(expectedCaptureObject.get(ATTRIBUTE_INDEX)));

    assertThat(captureObject.getDataIndex())
        .isEqualTo(Long.valueOf(expectedCaptureObject.get(DATA_INDEX)));
    assertThat(captureObject.getUnit())
        .isEqualTo(OsgpUnitType.valueOf(expectedCaptureObject.get(UNIT)));
  }

  private void logData(final PowerQualityProfileData powerQualityProfileData) {
    log.info("| classId | logicalName | attributeIndex | dataIndex | unit |");
    for (final CaptureObject captureObject :
        powerQualityProfileData.getCaptureObjectList().getCaptureObjects()) {
      log.info(
          "| {} | {} | {} | {} | {} |",
          captureObject.getClassId(),
          captureObject.getLogicalName(),
          captureObject.getAttributeIndex(),
          captureObject.getDataIndex(),
          captureObject.getUnit());
    }
  }
}
