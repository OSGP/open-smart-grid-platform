// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdemoapp.infra.platform;

import java.security.GeneralSecurityException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

/** Helper class to create WebServiceTemplates for each specific domain. */
public class SoapRequestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(SoapRequestHelper.class);

  private static final String ALLOW_ALL_HOSTNAMES = "ALLOW_ALL_HOSTNAMES";
  private static final String BROWSER_COMPATIBLE_HOSTNAMES = "BROWSER_COMPATIBLE_HOSTNAMES";

  @Value("${organisation.identification}")
  private String organisationIdentifcation;

  @Value("${user.name}")
  private String userName;

  @Value("${application.name}")
  private String applicationName;

  @Value("${base.uri}")
  private String baseUri;

  @Value("${web.service.template.default.uri.admin.devicemanagement}")
  private String adminWebServiceDeviceManagementUri;

  @Value("${web.service.template.default.uri.publiclighting.adhocmanagement}")
  private String publicLightingWebServiceAdHocManagementUri;

  @Value("${web.service.hostname.verification.strategy}")
  private String webServiceHostnameVerificationStrategy;

  private Jaxb2Marshaller marshaller;
  private final KeyStoreHelper keyStoreHelper;

  private final SaajSoapMessageFactory messageFactory;

  public SoapRequestHelper(
      final SaajSoapMessageFactory messageFactory, final KeyStoreHelper keyStoreHelper) {
    this.messageFactory = messageFactory;
    this.keyStoreHelper = keyStoreHelper;
  }

  /**
   * Helper function to create a web service template to handle soap requests for the Admin domain
   *
   * @return WebServiceTemplate
   */
  public WebServiceTemplate createAdminRequest() {
    this.initMarshaller("org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement");

    // Example URI:
    // "https://localhost/osgp-adapter-ws-admin/admin/deviceManagementService/DeviceManagement";
    final String uri = this.baseUri + this.adminWebServiceDeviceManagementUri;

    final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

    webServiceTemplate.setDefaultUri(uri);
    webServiceTemplate.setMarshaller(this.marshaller);
    webServiceTemplate.setUnmarshaller(this.marshaller);

    webServiceTemplate.setCheckConnectionForFault(true);

    webServiceTemplate.setInterceptors(
        new ClientInterceptor[] {
          this.createClientInterceptor("http://www.opensmartgridplatform.org/schemas/common")
        });

    webServiceTemplate.setMessageSender(this.createHttpMessageSender());

    return webServiceTemplate;
  }

  /**
   * Helper function to create a web service template to handle soap requests for the Public
   * Lighting domain
   *
   * @return WebServiceTemplate
   */
  public WebServiceTemplate createPublicLightingRequest() {
    this.initMarshaller(
        "org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement");

    // Example URI:
    // "https://localhost/osgp-adapter-ws-publiclighting/publiclighting/adHocManagementService/AdHocManagement";
    final String uri = this.baseUri + this.publicLightingWebServiceAdHocManagementUri;

    final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

    webServiceTemplate.setDefaultUri(uri);
    webServiceTemplate.setMarshaller(this.marshaller);
    webServiceTemplate.setUnmarshaller(this.marshaller);

    webServiceTemplate.setCheckConnectionForFault(true);

    webServiceTemplate.setInterceptors(
        new ClientInterceptor[] {
          this.createClientInterceptor("http://www.opensmartgridplatform.org/schemas/common")
        });

    webServiceTemplate.setMessageSender(this.createHttpMessageSender());

    return webServiceTemplate;
  }

  /** Initializes the JaxB Marshaller */
  private void initMarshaller(final String marshallerContext) {
    this.marshaller = new Jaxb2Marshaller();

    this.marshaller.setContextPath(marshallerContext);
  }

  /**
   * Creates a HttpComponentsMessageSender for communication with the platform.
   *
   * @return HttpComponentsMessageSender
   */
  private HttpComponentsMessageSender createHttpMessageSender() {

    final HttpComponentsMessageSender sender = new HttpComponentsMessageSender();

    final HttpClientBuilder builder = HttpClients.custom();
    builder.addInterceptorFirst(new ContentLengthHeaderRemoveInterceptor());
    try {
      final SSLContext sslContext =
          SSLContexts.custom()
              .loadKeyMaterial(
                  this.keyStoreHelper.getKeyStore(), this.keyStoreHelper.getKeyStorePwAsChar())
              .loadTrustMaterial(this.keyStoreHelper.getTrustStore(), new TrustSelfSignedStrategy())
              .build();

      final HostnameVerifier hostnameVerifier = this.getHostnameVerifier();

      final SSLConnectionSocketFactory sslConnectionFactory =
          new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
      builder.setSSLSocketFactory(sslConnectionFactory);
      sender.setHttpClient(builder.build());
    } catch (final GeneralSecurityException e) {
      LOGGER.error("Unbale to create SSL context", e);
    }

    return sender;
  }

  public HostnameVerifier getHostnameVerifier() throws GeneralSecurityException {
    if (ALLOW_ALL_HOSTNAMES.equals(this.webServiceHostnameVerificationStrategy)) {
      return new NoopHostnameVerifier();
    } else if (BROWSER_COMPATIBLE_HOSTNAMES.equals(this.webServiceHostnameVerificationStrategy)) {
      return new DefaultHostnameVerifier();
    } else {
      throw new GeneralSecurityException("No hostname verification strategy set!");
    }
  }

  /** Create a ClientIntercepter, used for the WebServiceTemplate. */
  private ClientInterceptor createClientInterceptor(final String namespace) {
    return new IdentificationClientInterceptor(
        this.organisationIdentifcation,
        this.userName,
        this.applicationName,
        namespace,
        "OrganisationIdentification",
        "UserName",
        "ApplicationName");
  }
}
