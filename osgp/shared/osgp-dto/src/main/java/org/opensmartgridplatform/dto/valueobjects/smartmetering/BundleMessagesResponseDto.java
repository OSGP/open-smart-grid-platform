// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BundleMessagesResponseDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 9084545073121917337L;

  private List<ActionResponseDto> actionValueObjectResponseDtoList;

  public BundleMessagesResponseDto(final List<ActionResponseDto> actionValueObjectResponseDtoList) {
    this.actionValueObjectResponseDtoList = actionValueObjectResponseDtoList;
  }

  public List<ActionResponseDto> getAllResponses() {
    return new ArrayList<>(this.actionValueObjectResponseDtoList);
  }
}
