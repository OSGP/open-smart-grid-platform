// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.usermanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;

/** Base class for web clients for the web-api-user-management web service. */
public class AbstractClient {

  protected WebClient webClient = null;

  protected ObjectMapper jacksonObjectMapper;

  protected static final String UTF_8 = "UTF-8";

  protected static final String HEADER_PARAM_REQUESTING_ORGANISATION_IDENTIFICATION =
      "requestingOrgId";
  protected static final String HEADER_PARAM_TOKEN = "token";

  protected static final String RESPONSE_IS_NULL = "response is null";
  protected static final String HTTP_STATUS_IS_NOT_200 =
      "httpStatusCode is not 200, httpStatusCode: ";
  protected static final String RESPONSE_BODY_IS_EMPTY = "response body is empty";

  /**
   * Get a WebClient instance from the configured WebClient.
   *
   * @return A new WebClient instance.
   */
  protected WebClient getWebClientInstance() {

    return WebClient.fromClient(this.webClient)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON)
        .encoding(UTF_8);
  }

  /**
   * Create the headers for a request.
   *
   * @param organisationIdentification The organisation identification of the organisation issuing
   *     the request.
   * @param token The authentication token.
   * @return A MultivaluedMap containing the headers for the request.
   */
  protected MultivaluedMap<String, String> createHeaders(
      final String organisationIdentification, final String token) {

    final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>(2);
    headers.add(HEADER_PARAM_REQUESTING_ORGANISATION_IDENTIFICATION, organisationIdentification);
    headers.add(HEADER_PARAM_TOKEN, token);
    return headers;
  }

  /**
   * Check the response of the User Management API.
   *
   * @param response The response.
   * @return The body of the response as string.
   * @throws ResponseException In case the response is null, the HTTP status code is not equal to
   *     200 OK or if the response body is empty.
   */
  protected String checkResponse(final Response response) throws ResponseException {

    if (response == null) {
      throw new ResponseException(RESPONSE_IS_NULL);
    }

    final int httpStatusCode = response.getStatus();
    if (httpStatusCode != 200) {
      throw new ResponseException(HTTP_STATUS_IS_NOT_200 + httpStatusCode);
    }

    final String body = response.readEntity(String.class);
    if (StringUtils.isEmpty(body)) {
      throw new ResponseException(RESPONSE_BODY_IS_EMPTY);
    }

    return body;
  }
}
