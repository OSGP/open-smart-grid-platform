package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.shared.services.NotificationService;
import com.alliander.osgp.adapter.ws.shared.services.ResendNotificationService;

@Service
@Transactional(value = "transactionManager")
public class ResendNotificationSmartmetering extends ResendNotificationService {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ResponseDataRepository responseDataRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationSmartmetering.class);

	public void executer(ResponseData responseData) {

		if (EnumUtils.isValidEnum(NotificationType.class, responseData.getMessageType())) {
			LOGGER.info("Found response data for resending notification");
			NotificationType notificationType = NotificationType.valueOf(responseData.getMessageType());
			this.notificationService.sendNotification(responseData.getOrganisationIdentification(),
					responseData.getDeviceIdentification(), responseData.getResultType().name(),
					responseData.getCorrelationUid(), getNotificationMessage(responseData.getMessageType()),
					notificationType);
			LOGGER.info("Notification has been resend");
			responseData.setNumberOfNotificationsSend(responseData.getNumberOfNotificationsSend() + 1);
			this.responseDataRepository.save(responseData);
		}
	}
}
