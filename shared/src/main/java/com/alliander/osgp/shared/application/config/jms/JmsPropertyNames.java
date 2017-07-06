/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config.jms;

/**
 * Common property names for JMS configuration classes.
 */
public class JmsPropertyNames {

    public static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    public static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    public static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    public static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    private JmsPropertyNames() {
        // Empty private constructor to prevent creating an instance of this
        // utility class.
    }
}
