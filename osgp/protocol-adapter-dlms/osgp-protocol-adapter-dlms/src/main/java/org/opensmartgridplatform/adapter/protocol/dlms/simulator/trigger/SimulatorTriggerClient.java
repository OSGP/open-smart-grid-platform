// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.MessageTrustDecider;
import org.apache.cxf.transport.http.URLConnectionInfo;
import org.apache.cxf.transport.http.UntrustedURLConnectionIOException;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.shared.usermanagement.AbstractClient;
import org.opensmartgridplatform.shared.usermanagement.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatorTriggerClient extends AbstractClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorTriggerClient.class);

  private static final String CONSTRUCTION_FAILED = "SimulatorTriggerClient construction failed";
  private static final String TRIGGERPATH = "trigger";
  private static final String DYNAMIC_ATTRIBUTES_PATH = "dynamic";

  /**
   * Construct a SimulatorTriggerClient instance.
   *
   * @param truststoreLocation The location of the trust store
   * @param truststorePassword The password for the trust store
   * @param truststoreType The type of the trust store
   * @param baseAddress The base address or URL for the SimulatorTriggerClient.
   * @throws SimulatorTriggerClientException In case the construction fails, a
   *     SimulatorTriggerClientException will be thrown.
   */
  public SimulatorTriggerClient(
      final String truststoreLocation,
      final String truststorePassword,
      final String truststoreType,
      final String baseAddress)
      throws SimulatorTriggerClientException {

    try (final InputStream stream = new FileInputStream(truststoreLocation)) {
      // Create the KeyStore.
      final var truststore = KeyStore.getInstance(truststoreType.toUpperCase());

      truststore.load(stream, truststorePassword.toCharArray());

      // Create TrustManagerFactory and initialize it using the KeyStore.
      final var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(truststore);

      // Create Apache CXF WebClient with JSON provider.
      final List<Object> providers = new ArrayList<>();
      providers.add(new JacksonJsonProvider());

      this.webClient = WebClient.create(baseAddress, providers);
      if (this.webClient == null) {
        throw new SimulatorTriggerClientException("webclient is null");
      }

      // Set up the HTTP Conduit to use the TrustManagers.
      final var config = WebClient.getConfig(this.webClient);
      final var conduit = config.getHttpConduit();

      conduit.setTlsClientParameters(new TLSClientParameters());
      conduit.getTlsClientParameters().setTrustManagers(tmf.getTrustManagers());
    } catch (final Exception e) {
      throw new SimulatorTriggerClientException(CONSTRUCTION_FAILED, e);
    }
  }

  /**
   * Creates a SimulatorTriggerClient that does not use a trust store and will trust any server it
   * communicates with over HTTPS.
   */
  public SimulatorTriggerClient(final String baseAddress) {
    LOGGER.warn("Configuring insecure web client. This should not be used in production!");

    this.webClient = this.configureInsecureWebClient(baseAddress);
  }

  /*
   * Client for simulator in use with test code only! For now don't check
   * or verify any certificates here.
   */
  private WebClient configureInsecureWebClient(final String baseAddress) {

    final List<Object> providers = new ArrayList<>();
    providers.add(new JacksonJsonProvider());

    final var client = WebClient.create(baseAddress, providers);

    final var config = WebClient.getConfig(client);
    final var conduit = config.getHttpConduit();

    final var tlsClientParameters = new TLSClientParameters();
    tlsClientParameters.setTrustManagers(
        new TrustManager[] {
          new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }

            @SuppressWarnings(
                "squid:S4830") // no server certification validation specifically for testing
            // purposes
            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType)
                throws CertificateException {
              /*
               * Implicitly trust the certificate chain by not throwing a
               * CertificateException.
               */
            }

            @SuppressWarnings(
                "squid:S4830") // no server certification validation specifically for testing
            // purposes
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType)
                throws CertificateException {
              /*
               * Implicitly trust the certificate chain by not throwing a
               * CertificateException.
               */
            }
          }
        });
    tlsClientParameters.setDisableCNCheck(true);

    LOGGER.info("Setting Tls Client Parameters.");
    conduit.setTlsClientParameters(tlsClientParameters);
    conduit.setTrustDecider(
        new MessageTrustDecider() {

          @Override
          public void establishTrust(
              String conduitName, URLConnectionInfo connectionInfo, Message message)
              throws UntrustedURLConnectionIOException {
            // Do Nothing
          }
        });

    return client;
  }

  public void sendTrigger(final DlmsDevice simulatedDlmsDevice)
      throws SimulatorTriggerClientException {

    this.checkResponse(
        this.getWebClientInstance()
            .path(TRIGGERPATH)
            .query("port", simulatedDlmsDevice.getPort())
            .query("logicalId", simulatedDlmsDevice.getLogicalId())
            .get(),
        "sendTrigger");
  }

  public void clearDlmsAttributeValues() throws SimulatorTriggerClientException {

    this.checkResponse(
        this.getWebClientInstance().path(DYNAMIC_ATTRIBUTES_PATH).delete(),
        "clearDlmsAttributeValues");
  }

  public void setDlmsAttributeValues(
      final int classId, final ObisCode obisCode, final ObjectNode jsonAttributeValues)
      throws SimulatorTriggerClientException {

    this.checkResponse(
        this.getWebClientInstance()
            .path(DYNAMIC_ATTRIBUTES_PATH)
            .path(classId)
            .path(obisCode.asDecimalString())
            .put(jsonAttributeValues),
        "setDlmsAttributeValues");
  }

  public void setDlmsAttributeValue(
      final int classId,
      final ObisCode obisCode,
      final int attributeId,
      final ObjectNode jsonAttributeValue)
      throws SimulatorTriggerClientException {

    this.checkResponse(
        this.getWebClientInstance()
            .path(DYNAMIC_ATTRIBUTES_PATH)
            .path(classId)
            .path(obisCode.asDecimalString())
            .path(attributeId)
            .put(jsonAttributeValue),
        "setDlmsAttributeValue");
  }

  public ObjectNode getDlmsAttributeValues(final int classId, final ObisCode obisCode)
      throws SimulatorTriggerClientException {

    final var response =
        this.getWebClientInstance()
            .path(DYNAMIC_ATTRIBUTES_PATH)
            .path(classId)
            .path(obisCode.asDecimalString())
            .get();

    this.checkResponse(response, "getDlmsAttributeValues");
    if (Status.NO_CONTENT.getStatusCode() == response.getStatus()) {
      return null;
    }
    return response.readEntity(ObjectNode.class);
  }

  public ObjectNode getDlmsAttributeValue(
      final int classId, final ObisCode obisCode, final int attributeId)
      throws SimulatorTriggerClientException {

    final var response =
        this.getWebClientInstance()
            .path(DYNAMIC_ATTRIBUTES_PATH)
            .path(classId)
            .path(obisCode.asDecimalString())
            .path(attributeId)
            .get();

    this.checkResponse(response, "getDlmsAttributeValue");
    if (Status.NO_CONTENT.getStatusCode() == response.getStatus()) {
      return null;
    }
    return response.readEntity(ObjectNode.class);
  }

  private void checkResponse(final Response response, final String checkedFrom)
      throws SimulatorTriggerClientException {

    try {
      this.checkResponseStatus(response);
    } catch (final ResponseException e) {
      throw new SimulatorTriggerClientException("Response exception from " + checkedFrom, e);
    }
  }

  private void checkResponseStatus(final Response response) throws ResponseException {

    if (response == null) {
      throw new ResponseException(RESPONSE_IS_NULL);
    }

    if (Status.Family.SUCCESSFUL != response.getStatusInfo().getFamily()) {
      throw new ResponseException(HTTP_STATUS_IS_NOT_200 + response.getStatus());
    }
  }
}
