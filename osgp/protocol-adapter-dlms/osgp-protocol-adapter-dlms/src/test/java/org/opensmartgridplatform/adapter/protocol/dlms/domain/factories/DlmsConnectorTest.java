/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnector.getExceptionWithExceptionType;

import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

class DlmsConnectorTest {
  private static final DlmsDevice device = new DlmsDevice("DeviceId");

  @ParameterizedTest
  @CsvSource({
    "Unknown failure,CONNECTION_ERROR",
    "AUTHENTICATION_ERROR,AUTHENTICATION_ERROR",
    "Unable to decypher/decrypt xDLMS pdu,UNABLE_TO_DECYPHER",
    "WRAPPER_HEADER_INVALID,WRAPPER_HEADER_ERROR",
    "UNKNOWN_ASSOCIATION_RESULT,UNKNOWN_ASSOCIATION_RESULT",
  })
  void testGetExceptionWithExceptionType(
      final String message, final FunctionalExceptionType expectedType) {
    final IOException exception = new IOException(message);

    final ConnectionException connectionException =
        getExceptionWithExceptionType(device, exception);

    assertThat(connectionException.getType()).isEqualTo(expectedType);
    assertThat(connectionException.getMessage()).contains("Message:" + message);
    assertThat(connectionException.getMessage())
        .contains("device " + device.getDeviceIdentification());
  }
}
