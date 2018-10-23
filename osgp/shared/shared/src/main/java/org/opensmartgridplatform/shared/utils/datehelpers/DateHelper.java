/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils.datehelpers;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

    private DateHelper() {
        // avoid creation of class
        // use only static methods.
    }

    public static Date getGmtDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        return cal.getTime();
    }

}
