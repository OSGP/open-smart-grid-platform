//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;

public class JdlmsObjectToStringUtilTest {

  @Test
  public void testDescribeGetResultWithSuccess() {

    final GetResult getResult =
        new GetResultImpl(DataObject.newInteger32Data(100), AccessResultCode.SUCCESS);

    final String description = JdlmsObjectToStringUtil.describeGetResult(getResult);

    assertThat(description).isEqualTo("SUCCESS, DOUBLE_LONG Value: 100");
  }

  @Test
  public void testDescribeGetResultWithNullResult() {

    final String description = JdlmsObjectToStringUtil.describeGetResult(null);

    assertThat(description).isEqualTo("Result is null");
  }

  @Test
  public void testDescribeGetResultWithNullResultCodeAndData() {

    final GetResult getResult = new GetResultImpl(null, null);

    final String description = JdlmsObjectToStringUtil.describeGetResult(getResult);

    assertThat(description).isEqualTo("Result code is null, Result data is null");
  }

  @Test
  public void testDescribeGetResults() {

    final GetResult getResult1 =
        new GetResultImpl(DataObject.newInteger32Data(100), AccessResultCode.SUCCESS);

    final GetResult getResult2 =
        new GetResultImpl(
            DataObject.newVisibleStringData("Test".getBytes()), AccessResultCode.OBJECT_UNDEFINED);

    final String description =
        JdlmsObjectToStringUtil.describeGetResults(Arrays.asList(getResult1, getResult2));

    assertThat(description)
        .isEqualTo(
            "{SUCCESS, DOUBLE_LONG Value: 100 - OBJECT_UNDEFINED, VISIBLE_STRING Value: Test}");
  }
}
