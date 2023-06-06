// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;

/**
 * Utility class that provides jDLMS object to text methods, that can be used for debugging or
 * logging purposes.
 */
public class JdlmsObjectToStringUtil {

  private JdlmsObjectToStringUtil() {
    // Do not instantiate utility class
  }

  public static String describeAttributes(final AttributeAddress... attributeAddresses) {
    if (attributeAddresses == null || attributeAddresses.length == 0) {
      return "";
    }
    final StringBuilder sb = new StringBuilder();
    for (final AttributeAddress attributeAddress : attributeAddresses) {
      sb.append(
          String.format(
              ", {%s,%s,%s}",
              attributeAddress.getClassId(),
              attributeAddress.getInstanceId(),
              attributeAddress.getId()));
    }
    return sb.substring(2);
  }

  public static String describeMethod(final MethodParameter methodParameter) {
    if (methodParameter == null) {
      return "";
    }
    return String.format(
        "{%d,%s,%d}",
        methodParameter.getClassId(), methodParameter.getInstanceId(), methodParameter.getId());
  }

  public static String describeGetResults(final List<GetResult> results) {
    return results.stream()
        .map(JdlmsObjectToStringUtil::describeGetResult)
        .collect(Collectors.joining(" - ", "{", "}"));
  }

  public static String describeGetResult(final GetResult result) {
    if (result != null) {
      final String code;
      final String data;
      if (result.getResultCode() != null) {
        code = result.getResultCode().toString();
      } else {
        code = "Result code is null";
      }
      if (result.getResultData() != null) {
        data = result.getResultData().toString();
      } else {
        data = "Result data is null";
      }
      return code + ", " + data;
    } else {
      return "Result is null";
    }
  }
}
