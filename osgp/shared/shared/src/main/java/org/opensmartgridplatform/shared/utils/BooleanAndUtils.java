/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

import java.util.Objects;

public class BooleanAndUtils {

    private boolean result = true;

    public BooleanAndUtils() {
        // Empty constructor
    }

    public BooleanAndUtils and(final boolean expression) {
        this.result &= expression;

        return this;
    }

    public BooleanAndUtils andCompare(final Object expressionA, final Object expressionB) {
        if (this.result) {
            this.result &= Objects.equals(expressionA, expressionB);
        }

        return this;
    }

    public boolean getResult() {
        return this.result;
    }

}
