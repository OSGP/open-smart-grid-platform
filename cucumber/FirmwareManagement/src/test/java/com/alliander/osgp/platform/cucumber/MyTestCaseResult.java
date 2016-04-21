package com.alliander.osgp.platform.cucumber;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;

public class MyTestCaseResult {
    private TestStepResult runTestStepByName;
    private WsdlTestCaseRunner results;

    public MyTestCaseResult(final TestStepResult runTestStepByName, final WsdlTestCaseRunner wsdlTestCaseRunner) {
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
