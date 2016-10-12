package org.osgpfoundation.osgp.webdemoapp.application.infra.platform;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import org.springframework.core.io.FileSystemResource;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

public class KeyStoreHelper {
	


	private String keyStorePw;
	
	private KeyStoreFactoryBean trustStoreFactory;
	
	private KeyStoreFactoryBean keyStoreFactory;
	
	public KeyStoreHelper (String trustStoreType, String trustStoreLoc, String trustStorePw, String keyStoreLocation, String keyStoreType, String keyStorePw) {

		this.keyStorePw = keyStorePw;
		
		this.trustStoreFactory = new KeyStoreFactoryBean();
		this.trustStoreFactory.setType(trustStoreType);
		this.trustStoreFactory.setLocation(new FileSystemResource(trustStoreLoc));
		this.trustStoreFactory.setPassword(trustStorePw);
		
		this.keyStoreFactory = new KeyStoreFactoryBean();
		this.keyStoreFactory.setType(keyStoreType);
		this.keyStoreFactory.setLocation(new FileSystemResource(keyStoreLocation));
		this.keyStoreFactory.setPassword(keyStorePw);
		try {
			this.keyStoreFactory.afterPropertiesSet();
			this.trustStoreFactory.afterPropertiesSet();
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public KeyStore getTrustStore () {
		return this.trustStoreFactory.getObject();
	}
	
	public KeyStore getKeyStore () {
		return this.keyStoreFactory.getObject();
	}
	
	public String getKeyStorePw () {
		return this.keyStorePw;
	}


	

}
