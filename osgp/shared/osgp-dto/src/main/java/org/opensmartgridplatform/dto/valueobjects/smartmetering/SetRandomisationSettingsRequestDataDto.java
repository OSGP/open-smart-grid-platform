/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetRandomisationSettingsRequestDataDto implements ActionRequestDto {

    private static final long serialVersionUID = 8564943872758612188L;

    private int directAttach;
    private int randomisationStartWindow;
    private int multiplicationFactor;
    private int numberOfRetries;

    public SetRandomisationSettingsRequestDataDto(int directAttach, int randomisationStartWindow,
            int multiplicationFactor, int numberOfRetries) {

        this.directAttach = directAttach;
        this.randomisationStartWindow = randomisationStartWindow;
        this.multiplicationFactor = multiplicationFactor;
        this.numberOfRetries = numberOfRetries;

    }

    public int getDirectAttach() {
        return directAttach;
    }

    public int getRandomisationStartWindow() {
        return randomisationStartWindow;
    }

    public int getMultiplicationFactor() {
        return multiplicationFactor;
    }

    public int getNumberOfRetries() {
        return numberOfRetries;
    }
}
