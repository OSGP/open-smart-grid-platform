//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue;

public class ActualPowerQualityResponseMappingTest {

  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped fields should have the same value.";
  private static final String MAPPED_LIST_SIZE_MESSAGE = "Mapped lists should have the same size.";

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  @Test
  public void shouldConvertActualPowerQualityResponse() {
    // Arrange
    final List<PowerQualityObject> powerQualityObjects = this.makePowerQualityObjects();
    final List<PowerQualityValue> powerQualityValues = this.makePowerQualityValues();

    final ActualPowerQualityData responseData =
        new ActualPowerQualityData(powerQualityObjects, powerQualityValues);

    final ActualPowerQualityResponse source = new ActualPowerQualityResponse(responseData);

    // Act
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
            .ActualPowerQualityResponse
        target =
            this.monitoringMapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .ActualPowerQualityResponse.class);

    // Assert
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ActualPowerQualityData
        mappedResponseData = target.getActualPowerQualityData();

    this.assertPowerQualityObjects(
        mappedResponseData.getPowerQualityObjects().getPowerQualityObject(), powerQualityObjects);
    this.assertPowerQualityValues(
        mappedResponseData.getPowerQualityValues().getPowerQualityValue(), powerQualityValues);
  }

  @Test
  public void sourceNull() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
            .ActualPowerQualityResponse
        target =
            this.monitoringMapper.map(
                null,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .ActualPowerQualityResponse.class);

    assertThat(target).isNull();
  }

  private void assertPowerQualityObject(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObject
          actualPowerQualityObject,
      final PowerQualityObject sourcePowerQualityObject)
      throws AssertionError {

    assertThat(actualPowerQualityObject.getName())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourcePowerQualityObject.getName());
    assertThat(actualPowerQualityObject.getUnit().value())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(sourcePowerQualityObject.getUnit());
  }

  private void assertPowerQualityObjects(
      final List<
              org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .PowerQualityObject>
          actualPowerQualityObjects,
      final List<PowerQualityObject> sourcePowerQualityObjects)
      throws AssertionError {

    assertThat(actualPowerQualityObjects.size())
        .as(MAPPED_LIST_SIZE_MESSAGE)
        .isEqualTo(sourcePowerQualityObjects.size());

    for (int i = 0; i < actualPowerQualityObjects.size(); i++) {
      this.assertPowerQualityObject(
          actualPowerQualityObjects.get(i), sourcePowerQualityObjects.get(i));
    }
  }

  private void assertPowerQualityValues(
      final List<
              org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .PowerQualityValue>
          powerQualityValues,
      final List<PowerQualityValue> sourcePowerQualityValues)
      throws AssertionError {

    assertThat(powerQualityValues.size())
        .as(MAPPED_LIST_SIZE_MESSAGE)
        .isEqualTo(sourcePowerQualityValues.size());

    for (int i = 0; i < powerQualityValues.size(); i++) {
      this.assertPowerQualityValue(powerQualityValues.get(i), sourcePowerQualityValues.get(i));
    }
  }

  private void assertPowerQualityValue(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityValue
          powerQualityValue,
      final PowerQualityValue sourcePowerQualityValue)
      throws AssertionError {

    final Object value = powerQualityValue.getStringValueOrDateValueOrFloatValue();
    if (value instanceof XMLGregorianCalendar) {
      final Date actualDate = ((XMLGregorianCalendar) value).toGregorianCalendar().getTime();
      assertThat(actualDate)
          .as(MAPPED_FIELD_VALUE_MESSAGE)
          .isEqualTo(sourcePowerQualityValue.getValue());
    } else {
      assertThat(value)
          .as(MAPPED_FIELD_VALUE_MESSAGE)
          .isEqualTo(sourcePowerQualityValue.getValue());
    }
  }

  private List<PowerQualityObject> makePowerQualityObjects() {
    return Arrays.asList(
        new PowerQualityObject("ATTR1", OsgpUnitType.UNDEFINED.name()),
        new PowerQualityObject("ATTR2", OsgpUnitType.W.name()),
        new PowerQualityObject("ATTR3", OsgpUnitType.W.name()),
        new PowerQualityObject("ATTR4", OsgpUnitType.W.name()));
  }

  private List<PowerQualityValue> makePowerQualityValues() {
    return this.makePowerQualityValue(
        new DateTime(2017, 1, 1, 1, 0, 0, DateTimeZone.UTC).toDate(),
        "test1",
        new BigDecimal(1.1d),
        111L);
  }

  private List<PowerQualityValue> makePowerQualityValue(final Serializable... values) {
    return Arrays.stream(values).map(PowerQualityValue::new).collect(Collectors.toList());
  }
}
