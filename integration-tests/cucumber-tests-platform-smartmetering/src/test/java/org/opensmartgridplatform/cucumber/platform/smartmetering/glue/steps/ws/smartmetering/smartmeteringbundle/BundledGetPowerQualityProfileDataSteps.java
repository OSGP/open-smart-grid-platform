// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityProfileData;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.builders.GetPowerQualityProfileRequestBuilder;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BitErrorRateType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SignalQualityType;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

@Slf4j
public class BundledGetPowerQualityProfileDataSteps extends BaseBundleSteps {

  public static final String CLASS_ID = "classId";

  public static final String LOGICAL_NAME = "logicalName";

  public static final String DESCRIPTION = "description";

  public static final String ATTRIBUTE_INDEX = "attributeIndex";

  public static final String DATA_INDEX = "dataIndex";

  public static final String UNIT = "unit";
  public static final String VALUE_TYPE = "value_type";

  private static final String LAST_RESPONSE = "last_response";

  @Given("^the bundle request contains a get power quality profile request with parameters$")
  public void theBundleRequestContainsAGetPowerQualityProfileRequestAction(
      final Map<String, String> parameters) throws Throwable {

    final GetPowerQualityProfileRequest action =
        new GetPowerQualityProfileRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then(
      "^the same bundle response should contain a power quality profile response with (\\d++) values for profile \"([^\"]*)\"$")
  public void theSameBundleResponseShouldContainAGetPowerQualityProfileResponse(
      final int nrOfValues, final String profileLogicalName, final DataTable valuesDataTable)
      throws Throwable {

    final Response response = (Response) ScenarioContext.current().get(LAST_RESPONSE);
    this.theBundleResponseShouldContainAGetPowerQualityProfileResponse(
        nrOfValues, profileLogicalName, response, valuesDataTable);
  }

  @Then(
      "^the bundle response should contain a power quality profile response with (\\d++) values for profile \"([^\"]*)\"$")
  public void theBundleResponseShouldContainAGetPowerQualityProfileResponse(
      final int nrOfValues, final String profileLogicalName, final DataTable valuesDataTable)
      throws Throwable {

    final Response response = this.getNextBundleResponse();
    ScenarioContext.current().put(LAST_RESPONSE, response);

    this.theBundleResponseShouldContainAGetPowerQualityProfileResponse(
        nrOfValues, profileLogicalName, response, valuesDataTable);
  }

  @Then("^the bundle response should contain an empty power quality profile response$")
  public void theBundleResponseShouldContainAnEmptyGetPowerQualityProfileResponse()
      throws GeneralSecurityException, IOException, WebServiceSecurityException {

    final Response response = this.getNextBundleResponse();
    ScenarioContext.current().put(LAST_RESPONSE, response);

    assertThat(response).isInstanceOf(GetPowerQualityProfileResponse.class);

    final GetPowerQualityProfileResponse getPowerQualityProfileResponse =
        (GetPowerQualityProfileResponse) response;

    assertThat(getPowerQualityProfileResponse.getPowerQualityProfileDatas()).isEmpty();
  }

  public void theBundleResponseShouldContainAGetPowerQualityProfileResponse(
      final int nrOfValues,
      final String profileLogicalName,
      final Response response,
      final DataTable valuesDataTable) {

    assertThat(response).isInstanceOf(GetPowerQualityProfileResponse.class);

    final GetPowerQualityProfileResponse getPowerQualityProfileResponse =
        (GetPowerQualityProfileResponse) response;
    final Optional<PowerQualityProfileData> optionalPowerQualityProfileData =
        getPowerQualityProfileResponse.getPowerQualityProfileDatas().stream()
            .filter(data -> this.matches(new ObisCode(profileLogicalName), data.getLogicalName()))
            .findFirst();

    assertThat(optionalPowerQualityProfileData).isPresent();
    final PowerQualityProfileData powerQualityProfileData = optionalPowerQualityProfileData.get();

    this.logData(powerQualityProfileData);

    final List<Map<String, String>> expectedCaptureObjects =
        valuesDataTable.asMaps(String.class, String.class);

    int columnNr = 0;
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

      final List<Object> valuesForCaptureObject =
          this.getValuesForCaptureObject(
              powerQualityProfileData.getProfileEntryList().getProfileEntries(), columnNr++);
      this.validateAllValues(
          valuesForCaptureObject,
          this.getExpectedCaptureObject(captureObject, expectedCaptureObjects));
    }
    assertThat(powerQualityProfileData.getCaptureObjectList().getCaptureObjects())
        .hasSize(expectedCaptureObjects.size());

    assertThat(powerQualityProfileData.getProfileEntryList().getProfileEntries())
        .hasSize(nrOfValues);
  }

  private List<Object> getValuesForCaptureObject(
      final List<ProfileEntry> profileEntries, final int columnNr) {
    final List<Object> valuesForCaptureObject = new ArrayList<>();
    for (final ProfileEntry profileEntry : profileEntries) {
      final ProfileEntryValue profileEntryValue = profileEntry.getProfileEntryValue().get(columnNr);
      assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue()).hasSize(1);
      valuesForCaptureObject.addAll(profileEntryValue.getStringValueOrDateValueOrFloatValue());
    }
    return valuesForCaptureObject;
  }

  private void validateAllValues(
      final List<Object> valuesForCaptureObject, final Map<String, String> expectedCaptureObject) {
    final ValueType valueType = ValueType.valueOf(expectedCaptureObject.get(VALUE_TYPE));
    final String description = expectedCaptureObject.get(DESCRIPTION);

    assertThat(valuesForCaptureObject)
        .isNotEmpty()
        .allMatch(value -> this.isOfValueOfType(value, valueType, description));
  }

  private boolean isOfValueOfType(
      final Object value, final ValueType valueType, final String description) {
    return switch (valueType) {
      case BER -> {
        BitErrorRateType.valueOf((String) value);
        yield value != null;
      }
      case SIGNAL_QUALITY -> {
        SignalQualityType.valueOf((String) value);
        yield true;
      }
      case DATE_TIME -> value instanceof XMLGregorianCalendar;
      case NUMBER -> value instanceof Long;
      case BIG_DECIMAL -> value instanceof BigDecimal;
    };
  }

  private boolean matches(final ObisCode obisCode, final ObisCodeValues logicalName) {
    final String obisCodeValue =
        String.format(
            "%d.%d.%d.%d.%d.%d",
            logicalName.getA(),
            logicalName.getB(),
            logicalName.getC(),
            logicalName.getD(),
            logicalName.getE(),
            logicalName.getF());
    return (obisCodeValue).equals(obisCode.asDecimalString());
  }

  private void assertCaptureObjectIsExpected(
      final CaptureObject captureObject, final List<Map<String, String>> expectedCaptureObjects) {
    final Map<String, String> expectedCaptureObject =
        this.getExpectedCaptureObject(captureObject, expectedCaptureObjects);

    assertThat(captureObject.getClassId())
        .isEqualTo(Long.valueOf(expectedCaptureObject.get(CLASS_ID)));
    assertThat(captureObject.getLogicalName()).isEqualTo(expectedCaptureObject.get(LOGICAL_NAME));
    assertThat(captureObject.getAttributeIndex())
        .isEqualTo(new BigInteger(expectedCaptureObject.get(ATTRIBUTE_INDEX)));

    assertThat(captureObject.getDataIndex())
        .isEqualTo(Long.valueOf(expectedCaptureObject.get(DATA_INDEX)));
    assertThat(captureObject.getUnit())
        .isEqualTo(OsgpUnitType.valueOf(expectedCaptureObject.get(UNIT)));

    this.validateValues(captureObject, expectedCaptureObjects);
  }

  private Map<String, String> getExpectedCaptureObject(
      final CaptureObject captureObject, final List<Map<String, String>> expectedCaptureObjects) {
    final Optional<Map<String, String>> optExpectedCaptureObject =
        expectedCaptureObjects.stream()
            .filter(
                d ->
                    d.get(LOGICAL_NAME).equals(captureObject.getLogicalName())
                        && new BigInteger(d.get(ATTRIBUTE_INDEX))
                            .equals(captureObject.getAttributeIndex())
                        && Long.parseLong(d.get(DATA_INDEX)) == captureObject.getDataIndex())
            .findFirst();
    assertThat(optExpectedCaptureObject).isPresent();
    return optExpectedCaptureObject.get();
  }

  private void validateValues(
      final CaptureObject captureObject, final List<Map<String, String>> expectedCaptureObjects) {}

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

  private enum ValueType {
    NUMBER,
    BIG_DECIMAL,
    SIGNAL_QUALITY,
    BER,
    DATE_TIME
  }
}
