/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.support.SoapUIException;

@Component
public class TestCaseRunner {

    public TestCaseResult runWsdlTestCase(final TestCase testCase, final Map<String, String> propertiesMap,
            final String testCaseNameRequest) throws XmlException, IOException, SoapUIException {
        final WsdlTestCase wsdlTestCase = (WsdlTestCase) testCase;

        for (final Entry<String, String> entry : propertiesMap.entrySet()) {
            wsdlTestCase.setPropertyValue(entry.getKey(), entry.getValue());
        }

        final WsdlTestCaseRunner wsdlTestCaseRunner = new WsdlTestCaseRunner(wsdlTestCase, new PropertiesMap());

        try {
            Thread.sleep(40000);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return new TestCaseResult(wsdlTestCaseRunner.runTestStepByName(testCaseNameRequest), wsdlTestCaseRunner);
    }
}
