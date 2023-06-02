//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BundleMessagesRequestDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -4268617729741836833L;

  private List<ActionDto> actionList;

  public BundleMessagesRequestDto(final List<ActionDto> actionList) {
    this.actionList = actionList;
  }

  public List<ActionDto> getActionList() {
    return new ArrayList<>(this.actionList);
  }

  public List<ActionResponseDto> getAllResponses() {
    final List<ActionResponseDto> responseDtoList = new ArrayList<>();

    for (final ActionDto actionDto : this.actionList) {
      final ActionResponseDto actionResponseDto = actionDto.getResponse();
      if (actionResponseDto != null) {
        responseDtoList.add(actionResponseDto);
      }
    }

    return responseDtoList;
  }
}
