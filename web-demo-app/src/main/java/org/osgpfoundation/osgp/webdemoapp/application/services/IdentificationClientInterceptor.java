package org.osgpfoundation.osgp.webdemoapp.application.services;

import javax.xml.namespace.QName;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class IdentificationClientInterceptor implements ClientInterceptor {
	
	private String orgId;
	private String userName;
	private String appName;
	private String namespace;
	private String orgIdHeaderName;
	private String userNameHeaderName;
	private String appNameHeaderName;
	
	public IdentificationClientInterceptor(String orgId, String userName, String appName, String namespace, String orgIdHeaderName, String userNameHeaderName, String appNameHeaderName) {
		this.orgId = orgId;
		this.userName = userName;
		this.appName = appName;
		this.namespace = namespace;
		this.orgIdHeaderName = orgIdHeaderName;
		this.userNameHeaderName = userNameHeaderName;
		this.appNameHeaderName = appNameHeaderName;
		
	}

	@Override
	public void afterCompletion(MessageContext arg0, Exception arg1)
			throws WebServiceClientException {
		
	}

	@Override
	public boolean handleFault(MessageContext arg0)
			throws WebServiceClientException {
		return true;
	}

	@Override
	public boolean handleRequest(MessageContext msgContext)
			throws WebServiceClientException {
		SoapMessage message = (SoapMessage) msgContext.getRequest();
		SoapHeader header = (SoapHeader) message.getSoapHeader ();
		
		QName orgIdHeaderHeaderQName = new QName(this.namespace, this.orgIdHeaderName);
		SoapHeaderElement orgElement = header.addHeaderElement(orgIdHeaderHeaderQName);
		orgElement.setText(orgId);
		
		QName appNameHeaderQName = new QName(this.namespace, this.appNameHeaderName);
		SoapHeaderElement appElement = header.addHeaderElement(appNameHeaderQName);
		appElement.setText(appName);
		
		QName userNameHeaderQName = new QName(this.namespace, this.userNameHeaderName);
		SoapHeaderElement userElement = header.addHeaderElement(userNameHeaderQName);
		userElement.setText(userName);
		
		return true;
	}

	@Override
	public boolean handleResponse(MessageContext arg0)
			throws WebServiceClientException {
		return true;
	}

}
