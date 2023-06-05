// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.mocks;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.List;
import org.opensmartgridplatform.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;
import org.springframework.beans.factory.annotation.Autowired;

public class RtuSchoteroogSteps implements RtuSteps {

  private static final int INDEX_LOGICAL_DEVICE_NAME = 0;
  private static final int INDEX_NODE_NAME = 1;
  private static final int INDEX_NODE_VALUE = 2;
  private static final int NUMBER_OF_INPUTS_FOR_MOCK_VALUE = 3;

  @Autowired private Iec61850MockServer iec61850MockServerSchoteroog;

  @Given("^the Schoteroog RTU returning$")
  @Override
  public void anRtuReturning(final List<List<String>> mockValues) throws Throwable {
    for (final List<String> mockValue : mockValues) {
      if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
        throw new AssertionError(
            "Mock value input rows from the Step DataTable must have "
                + NUMBER_OF_INPUTS_FOR_MOCK_VALUE
                + " elements.");
      }
      final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
      final String node = mockValue.get(INDEX_NODE_NAME);
      final String value = mockValue.get(INDEX_NODE_VALUE);

      this.iec61850MockServerSchoteroog.mockValue(logicalDeviceName, node, value);
    }
  }

  @Then("^the Schoteroog RTU should contain$")
  @Override
  public void theRtuShouldContain(final List<List<String>> mockValues) throws Throwable {
    for (final List<String> mockValue : mockValues) {
      if (NUMBER_OF_INPUTS_FOR_MOCK_VALUE != mockValue.size()) {
        throw new AssertionError(
            "Mock value input rows from the Step DataTable must have "
                + NUMBER_OF_INPUTS_FOR_MOCK_VALUE
                + " elements.");
      }
      final String logicalDeviceName = mockValue.get(INDEX_LOGICAL_DEVICE_NAME);
      final String node = mockValue.get(INDEX_NODE_NAME);
      final String value = mockValue.get(INDEX_NODE_VALUE);
      this.iec61850MockServerSchoteroog.assertValue(logicalDeviceName, node, value);
    }
  }
}
