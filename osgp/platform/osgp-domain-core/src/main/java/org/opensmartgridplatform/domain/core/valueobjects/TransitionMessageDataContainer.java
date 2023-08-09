// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class TransitionMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6687122715307445705L;

  private TransitionType transitionType;
  private ZonedDateTime dateTime;

  public void setTransitionType(final TransitionType transitionType) {
    this.transitionType = transitionType;
  }

  public void setDateTime(final ZonedDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public TransitionType getTransitionType() {
    return this.transitionType;
  }

  public ZonedDateTime getDateTime() {
    return this.dateTime;
  }
}
