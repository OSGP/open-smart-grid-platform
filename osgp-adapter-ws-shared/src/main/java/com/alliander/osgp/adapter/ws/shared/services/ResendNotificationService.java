package com.alliander.osgp.adapter.ws.shared.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;

@Service
@Transactional(value = "transactionManager")
public class ResendNotificationService {

	@Autowired
	private String resendNotificationCronExpression;

	@Autowired
	private int resendNotificationMultiplier;

	@Autowired
	private int resendNotificationMaximum;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ResponseDataRepository responseDataRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationService.class);

	public void execute() {
		List<ResponseData> notificationsToResend = new ArrayList<ResponseData>();
		notificationsToResend = this.responseDataRepository
				.findByNumberOfNotificationsSendLessThan(this.resendNotificationMaximum);
		for (ResponseData responseData : notificationsToResend) {
			if (EnumUtils.isValidEnum(NotificationType.class, responseData.getMessageType())) {
				int multiplier = this.resendNotificationMultiplier;
				if (responseData.getNumberOfNotificationsSend() == 0) {
					multiplier = 1;
				} else {
					multiplier = responseData.getNumberOfNotificationsSend() * multiplier;
				}

				Date modificationDate = responseData.getModificationTime();
				Date currentDate = new Date();
				long previousModificationTime = currentDate.getTime() - modificationDate.getTime();

				final CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(
						this.resendNotificationCronExpression);
				final Date nextFirstExecutionDate = cronSequenceGenerator.next(currentDate);
				final Date nextSecondExecutionDate = cronSequenceGenerator.next(nextFirstExecutionDate);
				final long cronSequenceInterval = nextSecondExecutionDate.getTime() - nextFirstExecutionDate.getTime();

				if ((cronSequenceInterval * multiplier) < previousModificationTime) {
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
	}

	private String getNotificationMessage(String responseData) {
		return String.format("Response of type %s is available.", responseData);
	}
}
