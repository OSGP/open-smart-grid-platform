// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

/**
 * {@link EndpointInterceptorAdapter} which checks the {@link TransportContext} for X509
 * certificates and stores RDN attribute values in the {@link MessageContext}.
 */
public class X509CertificateRdnAttributeValueEndpointInterceptor
    extends EndpointInterceptorAdapter {

  private final String attributeId;
  private final String contextPropertyName;

  /**
   * Creates an instance of {@link X509CertificateRdnAttributeValueEndpointInterceptor}.
   *
   * @param attributeId the RDN attribute ID of which the value will be stored in the {@link
   *     MessageContext}.
   * @param contextPropertyName the property name in which the RDN attribute values will be stored.
   */
  public X509CertificateRdnAttributeValueEndpointInterceptor(
      final String attributeId, final String contextPropertyName) {
    this.attributeId = attributeId;
    this.contextPropertyName = contextPropertyName;
  }

  /** {@inheritDoc} */
  @Override
  public boolean handleRequest(final MessageContext messageContext, final Object endpoint)
      throws Exception {
    final X509Certificate[] certificates =
        this.getX509CertificatesFromTransportContext(TransportContextHolder.getTransportContext());

    final Set<String> commonNames = new HashSet<>();

    for (final X509Certificate certificate : certificates) {
      final String commonName = this.getRdnAttributeValueFromX509Certificate(certificate);
      commonNames.add(commonName);
    }

    messageContext.setProperty(this.contextPropertyName, commonNames);

    return true;
  }

  /**
   * Gets the configured RDN attribute value from the given X509 certificate.
   *
   * @param certificate the certificate from which to get the RDN attribute value.
   * @return the RDN attribute value or an empty string if no value can be found or something goes
   *     wrong.
   */
  private String getRdnAttributeValueFromX509Certificate(final X509Certificate certificate) {
    try {
      final String subjectDn = certificate.getSubjectDN().getName();
      final LdapName ldapName = new LdapName(subjectDn);
      for (final Rdn rdn : ldapName.getRdns()) {
        final String rdnType = rdn.getType();
        if (rdnType.equalsIgnoreCase(this.attributeId)) {
          return (String) rdn.toAttributes().get(this.attributeId).get();
        }
      }
    } catch (final NamingException e) {
      this.logger.info("Getting CN from X509 certificate failed.", e);
    }

    return "";
  }

  /**
   * Get X509 certificates from a {@link TransportContext}.
   *
   * @param transportContext the context from which to get the certificates.
   * @return an array of certificates or an empty array if none can be found or something goes
   *     wrong.
   */
  private X509Certificate[] getX509CertificatesFromTransportContext(
      final TransportContext transportContext) {
    final HttpServletConnection connection =
        (HttpServletConnection) transportContext.getConnection();
    final Object x509CertificateAttribute =
        connection.getHttpServletRequest().getAttribute("javax.servlet.request.X509Certificate");

    if (x509CertificateAttribute instanceof X509Certificate[]) {
      return (X509Certificate[]) x509CertificateAttribute;
    } else {
      this.logger.info("HTTPServletRequest's attribute was not an array of X509Certificates.");
      return new X509Certificate[] {};
    }
  }
}
