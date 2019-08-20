/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutorDeviceContext;

class IdentificationNumberFactory {

    private static final int HEX_RADIX = 16;
    private static final String IDENTIFICATION_NUMBER_REGEX_SMR4 = "\\d{1,8}";
    private static final String IDENTIFICATION_NUMBER_REGEX_SMR5 = "[0-9A-Fa-f]{8}+";
    private static final long MIN_IDENTIFICATION = 0;
    private static final long MAX_IDENTIFICATION = Long.parseLong("99999999", HEX_RADIX);

    private final CommandExecutorDeviceContext deviceContext;

    private IdentificationNumberFactory(final CommandExecutorDeviceContext deviceContext) {
        this.deviceContext = deviceContext;
    }

    public static IdentificationNumberFactory create(CommandExecutorDeviceContext deviceContext) {
        return new IdentificationNumberFactory(deviceContext);
    }

    private void validateLast8Digits(final String last8Digits) {
        if (StringUtils.isNotBlank(last8Digits) && !matches(last8Digits)) {
            throw new IllegalArgumentException(
                    String.format("IdentificationNumber %s did not pass " + "validation.", last8Digits));
        }
    }

    private boolean matches(String last8Digits) {
        if (deviceContext.isSMR5()) {
            return last8Digits.matches(IDENTIFICATION_NUMBER_REGEX_SMR5);
        }
        return last8Digits.matches(IDENTIFICATION_NUMBER_REGEX_SMR4);
    }

    IdentificationNumber fromIdentification(final Long identification) {
        final String last8Digits = fromLong(identification);
        validateLast8Digits(last8Digits);

        return new IdentificationNumber(last8Digits);
    }

    IdentificationNumber fromLast8Digits(final String digits) {
        final String last8Digits = fromString(digits);
        validateLast8Digits(last8Digits);

        return new IdentificationNumber(last8Digits);
    }

    private String fromLong(final Long identification) {
        if (identification == null) {
            return null;
        }
        if (identification < MIN_IDENTIFICATION || identification > MAX_IDENTIFICATION) {
            throw new IllegalArgumentException(
                    String.format("identification not in [%d..%d]: %d", MIN_IDENTIFICATION, MAX_IDENTIFICATION,
                            identification));
        }
        return String.format("%08X", identification);
    }

    private String fromString(String digits) {
        if (StringUtils.isBlank(digits)) {
            return null;
        } else {
            /*
             * If a String of less than 8 digits is given, make sure it is
             * prefixed with zero digits up to a length of 8.
             */
            return String.format("%08d", Integer.valueOf(digits));
        }
    }

}
