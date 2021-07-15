/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import org.joda.time.DateTime;

public class TransitionMessageDataContainerDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5491018613060059335L;

  private TransitionTypeDto transitionType;
  private DateTime dateTime;

  public TransitionMessageDataContainerDto(
      final TransitionTypeDto transitionType, final DateTime dateTime) {
    this.transitionType = transitionType;
    this.dateTime = dateTime;
  }

  public TransitionTypeDto getTransitionType() {
    return this.transitionType;
  }

  public DateTime getDateTime() {
    return this.dateTime;
  }
}
