/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.support.ws.FaultDetailElement;
import com.alliander.osgp.cucumber.platform.support.ws.SoapFaultHelper;

/**
 * Class with generic web service response steps.
 */
public abstract class GenericResponseSteps extends GlueBase {

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
            assertEquals(getString(expected, PlatformKeys.KEY_FAULTCODE), faultCode);
        }
        if (expected.containsKey(PlatformKeys.KEY_FAULTSTRING)) {
            assertEquals(getString(expected, PlatformKeys.KEY_FAULTSTRING), faultString);
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

                assertEquals(localName, expectedValue, actualValue);
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
            assertEquals(localName, expectedValue.replaceAll("('.+\\d+:.+')", group), actualValue);
        } else {
            assertEquals(localName, expectedValue, actualValue);
        }
    }
}
