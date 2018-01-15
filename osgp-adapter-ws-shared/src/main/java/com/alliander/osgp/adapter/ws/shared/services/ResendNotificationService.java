package com.alliander.osgp.adapter.ws.shared.services;

public abstract class ResendNotificationService {

	public abstract void execute();

	public String getNotificationMessage(String responseData) {
		return String.format("Response of type %s is available.", responseData);
	}
}
