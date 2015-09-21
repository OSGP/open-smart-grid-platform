package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SynchronizeTimeReads implements Serializable {

	private static final long serialVersionUID = 2422648199174446889L;

	private String deviceIdentification;

	public String getDeviceIdentification() {
		return deviceIdentification;
	}

	public void setDeviceIdentification(String deviceIdentification) {
		this.deviceIdentification = deviceIdentification;
	}
	
}
