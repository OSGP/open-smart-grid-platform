package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SynchronizeTimeReadsRequest implements Serializable {

	private static final long serialVersionUID = 9010035251411605847L;

	private String deviceIdentification;

	public String getDeviceIdentification() {
		return deviceIdentification;
	}

	public void setDeviceIdentification(String deviceIdentification) {
		this.deviceIdentification = deviceIdentification;
	}

}
