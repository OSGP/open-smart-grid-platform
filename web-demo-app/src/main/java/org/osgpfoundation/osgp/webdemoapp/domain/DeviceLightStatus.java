package org.osgpfoundation.osgp.webdemoapp.domain;

public class DeviceLightStatus {
	
	private String deviceId;
	
	private int lightValue;
	
	private boolean lightOn;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	public int getLightValue() {
		return lightValue;
	}

	public void setLightValue(int lightValue) {
		this.lightValue = lightValue;
	}

	public boolean isLightOn() {
		return lightOn;
	}

	public void setLightOn(boolean lightOn) {
		this.lightOn = lightOn;
	}

}
