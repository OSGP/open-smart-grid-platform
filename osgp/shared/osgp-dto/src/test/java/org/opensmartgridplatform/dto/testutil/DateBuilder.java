/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.testutil;

import java.util.Date;

/** Creates instances, for testing purposes only. */
public class DateBuilder {
  private static int counter = 0;

  public Date build() {
    counter += 1;
    return new Date(24L * 60 * 60 * 1000 * counter);
  }
}
