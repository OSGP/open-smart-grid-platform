/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

public enum BitErrorRateDto {

    RXQUAL_0,
    RXQUAL_1,
    RXQUAL_2,
    RXQUAL_3,
    RXQUAL_4,
    RXQUAL_5,
    RXQUAL_6,
    RXQUAL_7,
    NOT_KNOWN_OR_NOT_DETECTABLE;

    public String value() {
        return name();
    }

    public static BitErrorRateDto fromValue(String v) {
        return valueOf(v);
    }

}
