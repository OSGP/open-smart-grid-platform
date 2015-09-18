package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SynchronizeTimeData implements Serializable {

	private static final long serialVersionUID = -5727904661110312803L;

	private String deviceIdentification;

	public String getDeviceIdentification() {
		return deviceIdentification;
	}

	public void setDeviceIdentification(String deviceIdentification) {
		this.deviceIdentification = deviceIdentification;
	}
	
}
