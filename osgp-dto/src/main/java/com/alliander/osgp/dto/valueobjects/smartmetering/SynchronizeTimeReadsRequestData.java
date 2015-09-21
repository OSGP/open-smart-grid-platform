package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SynchronizeTimeReadsRequestData implements Serializable {

	private static final long serialVersionUID = 2569975731784782614L;

    private String deviceIdentification;

	public String getDeviceIdentification() {
		return deviceIdentification;
	}

	public void setDeviceIdentification(String deviceIdentification) {
		this.deviceIdentification = deviceIdentification;
	}

}
