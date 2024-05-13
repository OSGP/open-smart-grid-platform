// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.HOSTNAME;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.PORT;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.PUSH_OBJECT_ATTRIBUTE_IDS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.PUSH_OBJECT_CLASS_IDS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.PUSH_OBJECT_DATA_INDEXES;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.PUSH_OBJECT_OBIS_CODES;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.PUSH_SETUP;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.PushSetupAttribute;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetPushSetupSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupSteps.class);

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  static void storeInScenario(final Map<String, String> settings, final String correlationUid) {
    ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, correlationUid);
    List.of(
            HOSTNAME,
            PORT,
            PUSH_OBJECT_ATTRIBUTE_IDS,
            PUSH_OBJECT_CLASS_IDS,
            PUSH_OBJECT_DATA_INDEXES,
            PUSH_OBJECT_OBIS_CODES)
        .stream()
        .forEach(key -> ScenarioContext.current().put(key, settings.get(key)));
  }

  void assertAttributeSetOnDevice(
      final PushSetupType pushSetupType, final Map<String, String> settings)
      throws WebServiceSecurityException {
    final GetSpecificAttributeValueResponse specificAttributeValues =
        this.getSpecificAttributeValues(pushSetupType, settings);
    checkGetSpecificAttributeValueResponse(specificAttributeValues);

    switch (pushSetupType.getAttribute()) {
      case SEND_DESTINATION_AND_METHOD:
        this.checkSendDestinationAndMethod(pushSetupType, specificAttributeValues);
        break;
      case PUSH_OBJECT_LIST:
        this.checkPushObjectList(pushSetupType, specificAttributeValues);
        break;
      case COMMUNICATION_WINDOW:
        this.checkCommunicationWindow(pushSetupType, specificAttributeValues);
        break;
    }
  }

  private void checkCommunicationWindow(
      final PushSetupType pushSetupType,
      final GetSpecificAttributeValueResponse specificAttributeValues) {
    final String actual = specificAttributeValues.getAttributeValueData();
    assertThat(actual)
        .as("{} was not set on device", pushSetupType.getName())
        .isEqualToIgnoringNewLines(
            "DataObject: Choice=ARRAY, ResultData isComplex, value=[java.util.LinkedList]: [\n]\n");
  }

  private void checkPushObjectList(
      final PushSetupType pushSetupType,
      final GetSpecificAttributeValueResponse specificAttributeValues) {
    final String expected = createExpectedPushObjectList();
    final String actual = specificAttributeValues.getAttributeValueData();
    assertThat(actual)
        .as("{} was not set on device", pushSetupType.getName())
        .isEqualToIgnoringNewLines(expected);
  }

  private static String createExpectedPushObjectList() {

    final List<String> pushObjectClassIds =
        Arrays.asList(getStringFromScenario(PUSH_OBJECT_CLASS_IDS).split(","));
    final List<String> pushObjectObisCodes =
        Arrays.asList(getStringFromScenario(PUSH_OBJECT_OBIS_CODES).split(","));
    final List<String> pushObjectAttributeIds =
        Arrays.asList(getStringFromScenario(PUSH_OBJECT_ATTRIBUTE_IDS).split(","));
    final List<String> pushObjectDataIndexes =
        Arrays.asList(getStringFromScenario(PUSH_OBJECT_DATA_INDEXES).split(","));

    assertThat(pushObjectClassIds).as("test request is expected to have 3 class ids").hasSize(3);
    assertThat(pushObjectObisCodes).as("test request is expected to have 3 obis codes").hasSize(3);
    assertThat(pushObjectAttributeIds)
        .as("test request is expected to have 3 attribute ids")
        .hasSize(3);
    assertThat(pushObjectDataIndexes)
        .as("test request is expected to have 3 data indexes")
        .hasSize(3);
    final String expected =
        String.format(
            "DataObject: Choice=ARRAY, ResultData isComplex, value=[java.util.LinkedList]: [\n"
                + "\tDataObject: Choice=STRUCTURE, ResultData isComplex, value=[java.util.LinkedList]: [\n"
                + "\tDataObject: Choice=LONG_UNSIGNED, ResultData isNumber, value=[java.lang.Integer]: %s\n"
                + "\tDataObject: Choice=OCTET_STRING, ResultData isByteArray, value=[[B]: logical name: %s\n"
                + "\tDataObject: Choice=INTEGER, ResultData isNumber, value=[java.lang.Byte]: %s\n"
                + "\tDataObject: Choice=LONG_UNSIGNED, ResultData isNumber, value=[java.lang.Integer]: %s\n]\n\n"
                + "\tDataObject: Choice=STRUCTURE, ResultData isComplex, value=[java.util.LinkedList]: [\n"
                + "\tDataObject: Choice=LONG_UNSIGNED, ResultData isNumber, value=[java.lang.Integer]: %s\n"
                + "\tDataObject: Choice=OCTET_STRING, ResultData isByteArray, value=[[B]: logical name: %s\n"
                + "\tDataObject: Choice=INTEGER, ResultData isNumber, value=[java.lang.Byte]: %s\n"
                + "\tDataObject: Choice=LONG_UNSIGNED, ResultData isNumber, value=[java.lang.Integer]: %s\n]\n\n"
                + "\tDataObject: Choice=STRUCTURE, ResultData isComplex, value=[java.util.LinkedList]: [\n"
                + "\tDataObject: Choice=LONG_UNSIGNED, ResultData isNumber, value=[java.lang.Integer]: %s\n"
                + "\tDataObject: Choice=OCTET_STRING, ResultData isByteArray, value=[[B]: logical name: %s\n"
                + "\tDataObject: Choice=INTEGER, ResultData isNumber, value=[java.lang.Byte]: %s\n"
                + "\tDataObject: Choice=LONG_UNSIGNED, ResultData isNumber, value=[java.lang.Integer]: %s\n]\n\n]\n",
            pushObjectClassIds.get(0),
            pushObjectObisCodes.get(0),
            pushObjectAttributeIds.get(0),
            pushObjectDataIndexes.get(0),
            pushObjectClassIds.get(1),
            pushObjectObisCodes.get(1),
            pushObjectAttributeIds.get(1),
            pushObjectDataIndexes.get(1),
            pushObjectClassIds.get(2),
            pushObjectObisCodes.get(2),
            pushObjectAttributeIds.get(2),
            pushObjectDataIndexes.get(2));
    return expected;
  }

  private static String getStringFromScenario(final String key) {
    return (String) ScenarioContext.current().get(key);
  }

  private void checkSendDestinationAndMethod(
      final PushSetupType pushSetupType,
      final GetSpecificAttributeValueResponse specificAttributeValues) {
    final String hostAndPort =
        ScenarioContext.current().get(PlatformSmartmeteringKeys.HOSTNAME)
            + ":"
            + ScenarioContext.current().get(PlatformSmartmeteringKeys.PORT);
    final byte[] expectedBytes = (hostAndPort.getBytes(StandardCharsets.US_ASCII));
    final String expected = Arrays.toString(expectedBytes);
    final String actual = specificAttributeValues.getAttributeValueData();
    assertThat(actual).as("{} was not set on device", pushSetupType.getName()).contains(expected);
  }

  private static void checkGetSpecificAttributeValueResponse(
      final GetSpecificAttributeValueResponse specificAttributeValues) {
    assertThat(specificAttributeValues)
        .as("GetSpecificAttributeValuesResponse was null")
        .isNotNull();
    assertThat(specificAttributeValues.getResult())
        .as("GetSpecificAttributeValuesResponse result was null")
        .isNotNull();
    assertThat(specificAttributeValues.getResult())
        .as("GetSpecificAttributeValuesResponse should be OK")
        .isEqualTo(OsgpResultType.OK);
  }

  private GetSpecificAttributeValueResponse getSpecificAttributeValues(
      final PushSetupType pushSetupType, final Map<String, String> settings)
      throws WebServiceSecurityException {

    final GetSpecificAttributeValueRequest request = new GetSpecificAttributeValueRequest();
    request.setClassId(BigInteger.valueOf(PUSH_SETUP.id()));
    request.setObisCode(this.getObisCodeValues(pushSetupType));
    request.setAttribute(BigInteger.valueOf(pushSetupType.getAttribute().attributeId()));
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final GetSpecificAttributeValueAsyncResponse asyncResponse =
        this.adHocRequestclient.doRequest(request);
    final GetSpecificAttributeValueAsyncRequest asyncRequest =
        new GetSpecificAttributeValueAsyncRequest();
    asyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());
    asyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
    return this.adHocResponseClient.getResponse(asyncRequest);
  }

  private ObisCodeValues getObisCodeValues(final PushSetupType pushSetupType) {
    final ObisCodeValues values = new ObisCodeValues();
    values.setA((short) 0);
    values.setB(pushSetupType.getObisCodeB());
    values.setC((short) 25);
    values.setD((short) 9);
    values.setE((short) 0);
    values.setF((short) 255);

    return values;
  }

  public enum PushSetupType {
    SMS((short) 2, "PushSetupSms", PushSetupAttribute.SEND_DESTINATION_AND_METHOD),
    ALARM((short) 1, "PushSetupAlarm", PushSetupAttribute.PUSH_OBJECT_LIST),
    LAST_GASP((short) 3, "PushSetupLastGasp", PushSetupAttribute.SEND_DESTINATION_AND_METHOD),
    UDP((short) 3, "PushSetupUdp", PushSetupAttribute.COMMUNICATION_WINDOW);

    private final Short obisCodeB;
    private final String name;
    private final PushSetupAttribute attribute;

    PushSetupType(final short obisCodeB, final String name, final PushSetupAttribute attribute) {
      this.obisCodeB = obisCodeB;
      this.name = name;
      this.attribute = attribute;
    }

    public Short getObisCodeB() {
      return this.obisCodeB;
    }

    public String getName() {
      return this.name;
    }

    public PushSetupAttribute getAttribute() {
      return this.attribute;
    }
  }
}
