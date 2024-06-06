/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;

@ExtendWith(MockitoExtension.class)
class OsgpExceptionConverterTest {

  @InjectMocks private OsgpExceptionConverter osgpExceptionConverter;

  @Test
  void testException() {
    final Exception exception = new Exception("Test Exception");

    final TechnicalException expectedException =
        new TechnicalException(
            ComponentType.PROTOCOL_DLMS,
            "Unexpected exception while handling protocol request/response message",
            new OsgpException(ComponentType.PROTOCOL_DLMS, exception.getMessage()));

    final OsgpException result =
        this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);

    this.assertThatExceptionEquals(result, expectedException);
  }

  @Test
  void testFunctionalException() {
    final FunctionalException exception =
        new FunctionalException(
            FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS);

    final FunctionalException expectedException =
        new FunctionalException(
            FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS);

    final OsgpException result =
        this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);

    this.assertThatExceptionEquals(result, expectedException);
  }

  @Test
  void testFunctionalExceptionWithProtocolAdapterException() {
    final FunctionalException exception =
        new FunctionalException(
            FunctionalExceptionType.SESSION_PROVIDER_ERROR,
            ComponentType.PROTOCOL_DLMS,
            new ProtocolAdapterException("protocol adapter exception message"));

    final FunctionalException expectedException =
        new FunctionalException(
            FunctionalExceptionType.SESSION_PROVIDER_ERROR,
            ComponentType.PROTOCOL_DLMS,
            new OsgpException(exception.getComponentType(), exception.getCause().getMessage()));

    final OsgpException result =
        this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);

    this.assertThatExceptionEquals(result, expectedException);
  }

  @Test
  void testProtocolAdapterException() {
    final ProtocolAdapterException exception = new ProtocolAdapterException("Test Exception");

    final TechnicalException expectedException =
        new TechnicalException(
            ComponentType.PROTOCOL_DLMS,
            "Unexpected exception while handling protocol request/response message",
            new OsgpException(ComponentType.PROTOCOL_DLMS, exception.getMessage()));

    final OsgpException result =
        this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);

    this.assertThatExceptionEquals(result, expectedException);
  }

  @Test
  void testConnectionException() {
    final ConnectionException exception = new ConnectionException("Test Exception");

    final FunctionalException expectedException =
        new FunctionalException(
            FunctionalExceptionType.CONNECTION_ERROR,
            ComponentType.PROTOCOL_DLMS,
            new OsgpException(ComponentType.PROTOCOL_DLMS, exception.getMessage()));

    final OsgpException result =
        this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);

    this.assertThatExceptionEquals(result, expectedException);
  }

  @Test
  void testNotSupportedByProtocolException() {
    final NotSupportedByProtocolException exception =
        new NotSupportedByProtocolException("Test Exception");

    final FunctionalException expectedException =
        new FunctionalException(
            FunctionalExceptionType.OPERATION_NOT_SUPPORTED_BY_PLATFORM_FOR_PROTOCOL,
            ComponentType.PROTOCOL_DLMS,
            new OsgpException(ComponentType.PROTOCOL_DLMS, exception.getMessage()));

    final OsgpException result =
        this.osgpExceptionConverter.ensureOsgpOrTechnicalException(exception);

    this.assertThatExceptionEquals(result, expectedException);
  }

  void assertThatExceptionEquals(
      final OsgpException result, final OsgpException expectedException) {
    assertThat(result.getComponentType()).isEqualTo(expectedException.getComponentType());
    assertThat(result.getMessage()).isEqualTo(expectedException.getMessage());
    if (expectedException.getCause() != null) {
      assertThat(result.getCause().getClass()).isEqualTo(expectedException.getCause().getClass());
      assertThat(result.getCause().getMessage())
          .isEqualTo(expectedException.getCause().getMessage());
    } else {
      assertNull(result.getCause());
    }
  }
}
