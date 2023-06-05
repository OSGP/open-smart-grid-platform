// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.rest;

import java.util.Arrays;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JasperError {
  INVALID_CREDENTIALS(
      "10000001",
      HttpStatus.UNAUTHORIZED,
      "Invalid credentials. Description: Control Center uses this error message when the API credentials are invalid or when the IP address is not within the allowed range."),
  INVALID_MESSAGEENCODING("10000017", HttpStatus.BAD_REQUEST, "Invalid messageEncoding."),
  INVALID_DATACODING("10000018", HttpStatus.BAD_REQUEST, "Invalid dataCoding."),
  INVALID_TPVP(
      "10000019",
      HttpStatus.BAD_REQUEST,
      "Invalid tpvp. Description: The validityPeriod is invalid."),
  MESSAGE_LENGTH_TOO_LONG(
      "10000020", HttpStatus.BAD_REQUEST, "Message length exceeds the maximum permissible length."),
  JSON_NOT_WELL_FORMED(
      "10000023",
      HttpStatus.BAD_REQUEST,
      "The JSON in the request is not well formed. Please ensure that commas, colons, braces etc. are formatted properly."),
  INVALID_APIVERSION("10000024", HttpStatus.BAD_REQUEST, "Invalid apiVersion."),
  INVALID_REQUEST(
      "10000028",
      HttpStatus.BAD_REQUEST,
      "Invalid request. Description: The request contained one or more unrecognized parameters."),
  NO_ACCESS_TO_API_FUNCTION(
      "10000030", HttpStatus.BAD_REQUEST, "Your role does not have access to this API function."),
  INVALID_ZONE("10000031", HttpStatus.BAD_REQUEST, "Invalid Zone."),
  INVALID_ICCID("20000001", HttpStatus.NOT_FOUND, "Resource not found - Invalid ICCID."),
  UNKNOWN_SERVER_ERROR("30000001", HttpStatus.INTERNAL_SERVER_ERROR, "Unknown server error."),
  CONTROL_CENTER_FAILED(
      "30000002",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Control Center failed to submit the message to the SMSC.");

  final String code;
  final HttpStatus httpStatus;
  final String message;

  JasperError(final String code, final HttpStatus httpStatus, final String message) {
    this.code = code;
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public static JasperError getByCode(final String code) {
    return Arrays.stream(JasperError.values())
        .filter(jasperError -> jasperError.getCode().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unexpected code: " + code));
  }
}
