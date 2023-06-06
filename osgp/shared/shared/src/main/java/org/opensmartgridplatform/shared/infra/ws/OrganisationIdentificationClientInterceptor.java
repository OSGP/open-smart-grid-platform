// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.ws;

import javax.xml.namespace.QName;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class OrganisationIdentificationClientInterceptor extends ClientInterceptorAdapter {
  private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
  private static final String USER_NAME_HEADER = "UserName";
  private static final String APPLICATION_NAME_HEADER = "ApplicationName";
  private static final String NAMESPACE_OSGP_COMMON =
      "http://www.opensmartgridplatform.org/schemas/common";

  private final String organisationIdentification;
  private final String userName;
  private final String applicationName;
  private final String namespace;
  private final String organisationIdentificationHeaderName;
  private final String userNameHeaderName;
  private final String applicationNameHeaderName;

  private OrganisationIdentificationClientInterceptor(final Builder builder) {
    this.organisationIdentification = builder.organisationIdentification;
    this.userName = builder.userName;
    this.applicationName = builder.applicationName;
    this.namespace = builder.namespace;
    this.organisationIdentificationHeaderName = builder.organisationIdentificationHeaderName;
    this.userNameHeaderName = builder.userNameHeaderName;
    this.applicationNameHeaderName = builder.applicationNameHeaderName;
  }

  public static class Builder {

    private String organisationIdentification = null;
    private String userName = null;
    private String applicationName = null;
    private String namespace = NAMESPACE_OSGP_COMMON;
    private String organisationIdentificationHeaderName = ORGANISATION_IDENTIFICATION_HEADER;
    private String userNameHeaderName = USER_NAME_HEADER;
    private String applicationNameHeaderName = APPLICATION_NAME_HEADER;

    public Builder withOrganisationIdentification(final String organisationIdentification) {
      this.organisationIdentification = organisationIdentification;
      return this;
    }

    public Builder withUserName(final String userName) {
      this.userName = userName;
      return this;
    }

    public Builder withApplicationName(final String applicationName) {
      this.applicationName = applicationName;
      return this;
    }

    public Builder withNamespace(final String namespace) {
      this.namespace = namespace;
      return this;
    }

    public Builder withOrganisationIdentificationHeaderName(
        final String organisationIdentificationHeaderName) {
      this.organisationIdentificationHeaderName = organisationIdentificationHeaderName;
      return this;
    }

    public Builder withUserNameHeaderName(final String userNameHeaderName) {
      this.userNameHeaderName = userNameHeaderName;
      return this;
    }

    public Builder withApplicationNameHeaderName(final String applicationNameHeaderName) {
      this.applicationNameHeaderName = applicationNameHeaderName;
      return this;
    }

    public OrganisationIdentificationClientInterceptor build() {
      return new OrganisationIdentificationClientInterceptor(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public boolean handleRequest(final MessageContext messageContext) {
    final SoapMessage soapMessage = (SoapMessage) messageContext.getRequest();
    final SoapHeader soapHeader = soapMessage.getSoapHeader();

    final QName headerName = new QName(this.namespace, this.organisationIdentificationHeaderName);
    final SoapHeaderElement element = soapHeader.addHeaderElement(headerName);
    element.setText(this.organisationIdentification);

    final QName qualifiedApplicationHeaderName =
        new QName(this.namespace, this.applicationNameHeaderName);
    final SoapHeaderElement applicationElement =
        soapHeader.addHeaderElement(qualifiedApplicationHeaderName);
    applicationElement.setText(this.applicationName);

    final QName qualifiedUserHeaderName = new QName(this.namespace, this.userNameHeaderName);
    final SoapHeaderElement userElement = soapHeader.addHeaderElement(qualifiedUserHeaderName);
    userElement.setText(this.userName);

    return true;
  }
}
