/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.ws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.support.ws.FaultDetailElement;
import org.opensmartgridplatform.cucumber.platform.support.ws.SoapFaultHelper;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Class with generic web service response steps.
 */
public abstract class GenericResponseSteps {

    private static String faultCode;
    private static String faultString;

    /**
     * Verify the soap fault in the ScenarioContext.Current().get(Keys.RESPONSE)
     *
     * @param expectedResult
     *            The list with expected result.
     */
    public static void verifySoapFault(final Map<String, String> expectedResult) {
        final SoapFaultClientException soapFault = (SoapFaultClientException) ScenarioContext.current()
                .get(PlatformKeys.RESPONSE);

        final QName qNameFaultCode = soapFault.getFaultCode();
        faultCode = qNameFaultCode.getPrefix() + ":" + qNameFaultCode.getLocalPart();
        faultString = soapFault.getFaultStringOrReason();

        final Object faultDetailValuesByElement = SoapFaultHelper.getFaultDetailValuesByElement(soapFault);

        assertFaultDetails(expectedResult, faultDetailValuesByElement);
    }

    private static void assertFaultDetails(final Map<String, String> expected, final Object actualObj) {

        if (expected.containsKey(PlatformKeys.KEY_FAULTCODE)) {
            assertThat(faultCode).isEqualTo(getString(expected, PlatformKeys.KEY_FAULTCODE));
        }
        if (expected.containsKey(PlatformKeys.KEY_FAULTSTRING)) {
            assertThat(faultString).isEqualTo(getString(expected, PlatformKeys.KEY_FAULTSTRING));
        }

        if (actualObj instanceof EnumMap) {
            @SuppressWarnings("unchecked")
            final Map<FaultDetailElement, String> actual = (Map<FaultDetailElement, String>) actualObj;
            for (final Map.Entry<String, String> expectedEntry : expected.entrySet()) {
                final String localName = expectedEntry.getKey();
                final FaultDetailElement faultDetailElement = FaultDetailElement.forLocalName(localName);
                if (faultDetailElement == null) {
                    /*
                     * Specified response parameter is not a FaultDetailElement
                     * (e.g. DeviceIdentification), skip it for the assertions.
                     */
                    continue;
                }

                final String expectedValue = expectedEntry.getValue();
                final String actualValue = actual.get(faultDetailElement);

                assertThat(actualValue).as(localName).isEqualTo(expectedValue);
            }
        } else if (actualObj instanceof ArrayList) {
            int externCounter = 0;
            for (final Map.Entry<String, String> expectedEntry : expected.entrySet()) {
                final String localName = expectedEntry.getKey();
                final FaultDetailElement faultDetailElement = FaultDetailElement.forLocalName(localName);
                if (faultDetailElement == null) {
                    /*
                     * Specified response parameter is not a FaultDetailElement
                     * (e.g. DeviceIdentification), skip it for the assertions.
                     */
                    continue;
                }

                if (expectedEntry.getValue().contains(";")) {
                    int internCounter = 0;
                    for (final String temp : expectedEntry.getValue().split(";")) {
                        assertExpectedAndActualValues(localName, temp, actualObj, internCounter);
                        internCounter++;
                    }
                } else {
                    assertExpectedAndActualValues(localName, expectedEntry, actualObj, externCounter);
                    externCounter++;
                }
            }
        }
    }

    private static void assertExpectedAndActualValues(final String localName,
            final Map.Entry<String, String> expectedEntry, final Object actual, final int counter) {

        final String expectedValue = expectedEntry.getValue();
        assertExpectedAndActualValues(localName, expectedValue, actual, counter);
    }

    private static void assertExpectedAndActualValues(final String localName, final String expectedValue,
            final Object actual, final int counter) {

        final Pattern pattern = Pattern.compile("('.+\\d+:.+')", Pattern.CASE_INSENSITIVE);
        @SuppressWarnings("unchecked")
        final String actualValue = ((List<String>) actual).get(counter);
        final Matcher matcher = pattern.matcher(actualValue);
        if (matcher.find()) {
            final String group = matcher.group(1);
            assertThat(actualValue).as(localName).isEqualTo(expectedValue.replaceAll("('.+\\d+:.+')", group));
        } else {
            assertThat(expectedValue).as(localName).isEqualTo(expectedValue);
        }
    }
}
