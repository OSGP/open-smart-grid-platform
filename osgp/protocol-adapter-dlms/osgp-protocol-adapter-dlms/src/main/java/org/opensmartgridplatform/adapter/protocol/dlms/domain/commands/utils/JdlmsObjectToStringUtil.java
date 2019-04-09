/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.MethodParameter;

/**
 * Utility class that provides jDLMS object to text methods, that can be used
 * for debugging or logging purposes.
 */
public class JdlmsObjectToStringUtil {

    private JdlmsObjectToStringUtil() {
        // Utility class.
    }

    public static String describeAttributes(final AttributeAddress... attributeAddresses) {

        if (attributeAddresses == null || attributeAddresses.length == 0) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        for (final AttributeAddress attributeAddress : attributeAddresses) {
            sb.append(", {").append(attributeAddress.getClassId()).append(',').append(attributeAddress.getInstanceId())
                    .append(',').append(attributeAddress.getId()).append('}');
        }
        return sb.substring(2);
    }

    public static String describeMethod(final MethodParameter methodParameter) {

        if (methodParameter == null) {
            return "";
        }

        return "{" + methodParameter.getClassId() + ',' + methodParameter.getInstanceId() + ',' + methodParameter
                .getId() + '}';
    }
}
