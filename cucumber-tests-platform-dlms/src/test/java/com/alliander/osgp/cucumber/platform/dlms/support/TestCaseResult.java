/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;

@Deprecated
public class TestCaseResult {
    private TestStepResult runTestStepByName;
    private WsdlTestCaseRunner results;

    public TestCaseResult(final TestStepResult runTestStepByName, final WsdlTestCaseRunner wsdlTestCaseRunner) {
        this.results = wsdlTestCaseRunner;
        this.runTestStepByName = runTestStepByName;
    }

    public TestStepResult getRunTestStepByName() {
        return this.runTestStepByName;
    }

    public WsdlTestCaseRunner getResults() {
        return this.results;
    }
}
