package org.osgpfoundation.osgp.webdemoapp.application.services;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

public class SoapRequestHelper {
	private Jaxb2Marshaller marshaller;
	private KeyStoreHelper keyStoreHelper;

	private SaajSoapMessageFactory messageFactory;

	public SoapRequestHelper(SaajSoapMessageFactory messageFactory, KeyStoreHelper keyStoreHelper) {
				
		this.messageFactory = messageFactory;
		this.keyStoreHelper = keyStoreHelper;
	}


	public WebServiceTemplate createUpdateKeyRequest() {
		initMarshaller("com.alliander.osgp.platform.ws.schema.admin.devicemanagement");

		String uri = "https://localhost/osgp-adapter-ws-admin/admin/deviceManagementService/DeviceManagement";

		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

		webServiceTemplate.setDefaultUri(uri);
		webServiceTemplate.setMarshaller(marshaller);
		webServiceTemplate.setUnmarshaller(marshaller);

		webServiceTemplate.setCheckConnectionForFault(true);
		
		webServiceTemplate.setInterceptors(new ClientInterceptor[] { 
						createClientInterceptor("http://www.alliander.com/schemas/osp/common") 
						});

		webServiceTemplate.setMessageSender(createHttpMessageSender());

		return webServiceTemplate;
	}
	
	
	public WebServiceTemplate createFindAllDevicesRequest() {
		initMarshaller("com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement");

		String uri = "https://localhost/osgp-adapter-ws-publiclighting/publiclighting/adHocManagementService/AdHocManagement";

		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

		webServiceTemplate.setDefaultUri(uri);
		webServiceTemplate.setMarshaller(marshaller);
		webServiceTemplate.setUnmarshaller(marshaller);

	
		webServiceTemplate.setCheckConnectionForFault(true);
		
		webServiceTemplate.setInterceptors(new ClientInterceptor[] { 
						createClientInterceptor("http://www.alliander.com/schemas/osgp/common") 
						});

		webServiceTemplate.setMessageSender(createHttpMessageSender());

		return webServiceTemplate;
	}
	
	public WebServiceTemplate createSetLightRequest() {
		initMarshaller("com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement");

		String uri = "https://localhost/osgp-adapter-ws-publiclighting/publiclighting/adHocManagementService/AdHocManagement";

		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

		webServiceTemplate.setDefaultUri(uri);
		webServiceTemplate.setMarshaller(marshaller);
		webServiceTemplate.setUnmarshaller(marshaller);

	
		webServiceTemplate.setCheckConnectionForFault(true);
		
		webServiceTemplate.setInterceptors(new ClientInterceptor[] { 
						createClientInterceptor("http://www.alliander.com/schemas/osgp/common") 
						});

		webServiceTemplate.setMessageSender(createHttpMessageSender());

		return webServiceTemplate;
	}

	private void initMarshaller(String marshallerContext) {
		this.marshaller = new Jaxb2Marshaller();
		
		marshaller.setContextPath(marshallerContext);
	}

	private HttpComponentsMessageSender createHttpMessageSender() {

		
		HttpComponentsMessageSender sender = new HttpComponentsMessageSender();
		HttpClient client = sender.getHttpClient();
		SSLSocketFactory socketFactory;
		try {			
			socketFactory = new SSLSocketFactory(
					keyStoreHelper.getKeyStore(),
					keyStoreHelper.getKeyStorePw(),
					keyStoreHelper.getTrustStore()
					);
			
			Scheme scheme = new Scheme("https", 443, socketFactory);
			
			client.getConnectionManager().getSchemeRegistry().register(scheme);
			
		} catch (KeyManagementException | UnrecoverableKeyException
				| NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			System.err.println("EXCEPTION!!!!!");
			e.printStackTrace();
		}

		return sender;
	}

	private ClientInterceptor createClientInterceptor(String namespace) {
		return new IdentificationClientInterceptor("test-org", "demo-app-user",
				"demo-app", namespace, "OrganisationIdentification",
				"UserName", "ApplicationName");
	}

}
