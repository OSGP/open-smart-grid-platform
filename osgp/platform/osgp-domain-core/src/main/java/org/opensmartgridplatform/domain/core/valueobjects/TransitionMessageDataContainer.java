// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import org.joda.time.DateTime;

public class TransitionMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6687122715307445705L;

  private TransitionType transitionType;
  private DateTime dateTime;

  public void setTransitionType(final TransitionType transitionType) {
    this.transitionType = transitionType;
  }

  public void setDateTime(final DateTime dateTime) {
    this.dateTime = dateTime;
  }

  public TransitionType getTransitionType() {
    return this.transitionType;
  }

  public DateTime getDateTime() {
    return this.dateTime;
  }
}
