package org.opensmartgridplatform.dto.da.mqtt;

import java.io.Serializable;

public class SubscriptionDto implements Serializable {

    private static final long serialVersionUID = 2044798509022724857L;

    private String host;
    private int port;
    private String topic;
}
