/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.wsheaderattribute.priority;

public enum MessagePriority {
    // Source: http://activemq.apache.org/how-can-i-support-priority-queues.html
    // The full range of priority values (0-9) are supported by the JDBC message
    // store. For KahaDB three priority categories are supported, Low (< 4),
    // Default (= 4) and High (> 4).
    LOW(1),
    DEFAULT(4),
    HIGH(7);

    private int priority;

    private MessagePriority(final int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

}
