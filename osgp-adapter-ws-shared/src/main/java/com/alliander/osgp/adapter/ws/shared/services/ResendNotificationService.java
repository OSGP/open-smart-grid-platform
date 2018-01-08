package com.alliander.osgp.adapter.ws.shared.services;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;

@EnableScheduling
@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
public class ResendNotificationService {

	private static final String KEY_RESEND_NOTIFICATION_MAXIMUM = "smartmetering.scheduling.job.resend.notification.maximum";

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ResponseDataRepository responseDataRepository;

	public void execute() {
		List<ResponseData> notificationsToResend = new ArrayList<ResponseData>();
		notificationsToResend = this.responseDataRepository
				.findByNumberOfNotificationsSendLessThan(KEY_RESEND_NOTIFICATION_MAXIMUM);

		// check modified by time with respect to number_of_notifications_send here?

		for (ResponseData responseData : notificationsToResend) {
			NotificationType notificationType = NotificationType.valueOf(responseData.getMessageType());
			this.notificationService.sendNotification(responseData.getOrganisationIdentification(),
					responseData.getDeviceIdentification(), responseData.getResultType().name(),
					responseData.getCorrelationUid(), getNotificationMessage(responseData.getMessageType()),
					notificationType);
			//if getNumberOfNotificationsSend != null, do following, or just fill in 1.
			responseData.setNumberOfNotificationsSend(responseData.getNumberOfNotificationsSend() + 1);
			// add +1 to number_of_notifications_send, update modification
			// time(automatically).
			this.responseDataRepository.save(responseData);
		}

	}

	private String getNotificationMessage(String responseData) {
		return String.format("Response of type %s is available.", responseData);
	}
}
