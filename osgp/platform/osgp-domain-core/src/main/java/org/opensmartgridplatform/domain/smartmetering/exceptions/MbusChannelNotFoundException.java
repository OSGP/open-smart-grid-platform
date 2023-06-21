// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.smartmetering.exceptions;

public class MbusChannelNotFoundException extends Exception {

  private static final long serialVersionUID = 7847147074103429934L;

  public MbusChannelNotFoundException(final String message) {
    super(message);
  }
}
