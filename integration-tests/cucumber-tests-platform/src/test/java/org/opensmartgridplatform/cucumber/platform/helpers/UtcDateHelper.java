/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtcDateHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(UtcDateHelper.class);

  public static Date getUtcDate() {
    final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    final SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    try {
      return dateFormatLocal.parse(dateFormatGmt.format(new Date()));
    } catch (final ParseException e) {
      LOGGER.error("error getting UTC date " + e);
      throw new RuntimeException(e.getMessage());
    }
  }

  private UtcDateHelper() {}
}
