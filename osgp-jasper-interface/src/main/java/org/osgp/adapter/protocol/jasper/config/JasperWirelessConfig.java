/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.jasper.config;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osgp.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.osgp.adapter.protocol.jasper.infra.ws.CorrelationIdProviderService;
import org.osgp.adapter.protocol.jasper.infra.ws.JasperWirelessSmsClient;
import org.osgp.adapter.protocol.jasper.infra.ws.JasperWirelessTerminalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

/**
 * An application context Java configuration class for Jasper Wireless settings.
 * The usage of Java configuration requires Spring Framework 3.0
 */
@Configuration
@PropertySource("file:${osp/osgpJasper/config}")
@ComponentScan(basePackages = { "org.osgp.adapter.protocol.jasper" })
public class JasperWirelessConfig {

	// JMS Settings
	private static final String PROPERTY_NAME_CONTROLCENTER_TERMINAL_URI = "jwcc.uri.terminal";
	private static final String PROPERTY_NAME_CONTROLCENTER_SMS_URI = "jwcc.uri.sms";
	private static final String PROPERTY_NAME_CONTROLCENTER_LICENSEKEY = "jwcc.licensekey";
	private static final String PROPERTY_NAME_CONTROLCENTER_USERNAME = "jwcc.username";
	private static final String PROPERTY_NAME_CONTROLCENTER_PASSWORD = "jwcc.password";
	private static final String PROPERTY_NAME_CONTROLCENTER_API_VERSION = "jwcc.api_version";
	private static final String GETSESSION_RETRIES = "jwcc.getsession.retries";
	private static final String GETSESSION_SLEEP_BETWEEN_RETRIES = "jwcc.getsession.sleep.between.retries";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JasperWirelessConfig.class);

	public JasperWirelessConfig() {
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths("com.jasperwireless.api.ws.service");
		return marshaller;
	}

	@Bean
	public SaajSoapMessageFactory messageFactory() throws OsgpJasperException {
		SaajSoapMessageFactory saajSoapMessageFactory = null;
		try {
			saajSoapMessageFactory = new SaajSoapMessageFactory(
					MessageFactory.newInstance());
			saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
		} catch (final SOAPException e) {
			final String msg = "Error in creating a webservice message wrapper";
			LOGGER.error(msg, e);
			throw new OsgpJasperException(msg, e);
		}
		return saajSoapMessageFactory;
	}

	@Bean
	public Wss4jSecurityInterceptor wss4jSecurityInterceptorClient() {
		final Wss4jSecurityInterceptor wss4jSecurityInterceptor = new Wss4jSecurityInterceptor();
		wss4jSecurityInterceptor.setSecurementActions("UsernameToken");
		return wss4jSecurityInterceptor;
	}

	@Bean
	public WebServiceTemplate webServiceTemplate() throws OsgpJasperException {
		final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
		webServiceTemplate.setMarshaller(this.marshaller());
		webServiceTemplate.setUnmarshaller(this.marshaller());
		webServiceTemplate
				.setDefaultUri("https://kpnapi.jasperwireless.com/ws/service/Sms");
		webServiceTemplate.setInterceptors(new ClientInterceptor[] { this
				.wss4jSecurityInterceptorClient() });
		webServiceTemplate.setMessageFactory(this.messageFactory());
		return webServiceTemplate;
	}

	@Bean
	public JasperWirelessSmsClient jasperWirelessSmsClient() {
		return new JasperWirelessSmsClient();
	}

	@Bean
	public JasperWirelessTerminalClient jasperWirelessTerminalClient() {
		return new JasperWirelessTerminalClient();
	}

	@Bean
	public JasperWirelessAccess jasperWirelessAccess(
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_SMS_URI + "}") final String uri,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_LICENSEKEY + "}") final String key,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_USERNAME + "}") final String username,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_PASSWORD + "}") final String password,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_API_VERSION + "}") final String version) {

		return new JasperWirelessAccess(uri, key, username, password, version);
	}

	@Bean
	public JasperWirelessAccess jasperWirelessTerminalAccess(
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_TERMINAL_URI + "}") final String uri,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_LICENSEKEY + "}") final String key,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_USERNAME + "}") final String username,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_PASSWORD + "}") final String password,
			@Value("${" + PROPERTY_NAME_CONTROLCENTER_API_VERSION + "}") final String version) {
		return new JasperWirelessAccess(uri, key, username, password, version);
	}

	@Bean
	public CorrelationIdProviderService correlationIdProviderService() {
		return new CorrelationIdProviderService();
	}

	@Bean
	public int jasperGetSessionRetries(
			@Value("${" + GETSESSION_RETRIES + "}") final int retries) {
		return retries;
	}

	@Bean
	public int jasperGetSessionSleepBetweenRetries(@Value("${"
			+ GETSESSION_SLEEP_BETWEEN_RETRIES + "}") final int sleep) {
		return sleep;
	}
}
