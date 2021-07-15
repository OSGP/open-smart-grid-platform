/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

/** Collection of JMS message field names. */
public class Constants {

  /** JMS constant for message field: OrganisationIdentification. */
  public static final String ORGANISATION_IDENTIFICATION = "OrganisationIdentification";

  /** JMS constant for message field: UserName. */
  public static final String USER_NAME = "UserName";

  /** JMS constant for message field: ApplicationName. */
  public static final String APPLICATION_NAME = "ApplicationName";

  /** JMS constant for message field: DeviceIdentification. */
  public static final String DEVICE_IDENTIFICATION = "DeviceIdentification";

  /** JMS constant for message field: IPAddress. */
  public static final String IP_ADDRESS = "IPAddress";

  /** JMS constant for message field: Domain. */
  public static final String DOMAIN = "Domain";

  /** JMS constant for message field: DomainVersion. */
  public static final String DOMAIN_VERSION = "DomainVersion";

  /** JMS constant for message field: Result. */
  public static final String RESULT = "Result";

  /** JMS constant for message field: Description. */
  public static final String DESCRIPTION = "Description";

  /** JMS constant for message field: TransitionType. */
  public static final String TRANSITION_TYPE = "TransitionType";

  /** JMS constant for message field: TransitionTime. */
  public static final String TRANSITION_TIME = "TransitionTime";

  /** JMS constant for message field: DeviceCity. */
  public static final String DEVICE_CITY = "DeviceCity";

  /** JMS constant for message field: DeviceStreet. */
  public static final String DEVICE_STREET = "DeviceStreet";

  /** JMS constant for message field: DeviceNumber. */
  public static final String DEVICE_NUMBER = "DeviceNumber";

  /** JMS constant for message field: Timestamp. */
  public static final String TIME_STAMP = "Timestamp";

  /** JMS constant for message field: ClassName. */
  public static final String CLASS_NAME = "ClassName";

  /** JMS constant for message field: MethodName. */
  public static final String METHOD_NAME = "MethodName";

  /** JMS constant for message field: ResponseResult. */
  public static final String RESPONSE_RESULT = "ResponseResult";

  /** JMS constant for message field: ResponseDataSize. */
  public static final String RESPONSE_DATA_SIZE = "ResponseDataSize";

  /** JMS constant for message field: DLMS_LOG_ITEM. */
  public static final String DLMS_LOG_ITEM_REQUEST = "DLMS_LOG_ITEM";

  /** JMS constant for message field: CORE_LOG_ITEM. */
  public static final String CORE_LOG_ITEM_REQUEST = "CORE_LOG_ITEM";

  /** JMS constant for message field: IEC61850_LOG_ITEM. */
  public static final String IEC61850_LOG_ITEM_REQUEST = "IEC61850_LOG_ITEM";

  /** JMS constant for message field: IEC60870_LOG_ITEM. */
  public static final String IEC60870_LOG_ITEM_REQUEST = "IEC60870_LOG_ITEM";

  /** JMS constant for message field: OSLP_LOG_ITEM. */
  public static final String OSLP_LOG_ITEM_REQUEST = "OSLP_LOG_ITEM";

  /** JMS constant for message field: IsIncoming. */
  public static final String IS_INCOMING = "IsIncoming";

  /** JMS constant for message field: DeviceUid. */
  public static final String DEVICE_UID = "DeviceUid";

  /** JMS constant for message field: EncodedMessage. */
  public static final String ENCODED_MESSAGE = "EncodedMessage";

  /** JMS constant for message field: DecodedMessage. */
  public static final String DECODED_MESSAGE = "DecodedMessage";

  /** JMS constant for message field: IsValid. */
  public static final String IS_VALID = "IsValid";

  /** JMS constant for message field: PayloadMessageSerializedSize. */
  public static final String PAYLOAD_MESSAGE_SERIALIZED_SIZE = "PayloadMessageSerializedSize";

  /** JMS constant for message field: ScheduleTime. */
  public static final String SCHEDULE_TIME = "ScheduleTime";

  /** JMS constant for message field: ScheduleTime. */
  public static final String IS_SCHEDULED = "Scheduled";

  /** JMS constant for message field: ScheduleTime. */
  public static final String DEVICE_TYPE = "DeviceType";

  /** JMS constant for message field: RetryCount. */
  public static final String RETRY_COUNT = "RetryCount";

  /** JMS constant for message field: RetryCount. */
  public static final String INNER_RETRY_COUNT = "InnerRetryCount";

  /** JMS constant for message field: MaxRetries. */
  public static final String MAX_RETRIES = "MaxRetries";

  /** JMS constant for message field: BypassRetry. */
  public static final String BYPASS_RETRY = "BypassRetry";

  /** JMS constant for message group. */
  public static final String MESSAGE_GROUP = "JMSXGroupID";

  /** JMS constant for delivery count of the message. */
  public static final String DELIVERY_COUNT = "JMSXDeliveryCount";

  // === DEPRECATED ===

  /** DEPRECATED JMS constant for message field: ErrorMessage. */
  public static final String ERROR_MESSAGE = "ErrorMessage";

  // === CTOR ===
  private Constants() {
    // Empty private constructor to prevent creating an instance of this
    // utility class.
  }
}
