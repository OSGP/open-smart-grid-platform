// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingSetScheduleRequest extends AbstractEntity {

  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  @Getter
  private String deviceIdentification;

  @Column(nullable = false)
  @Getter
  private String deviceUid;

  @Column(nullable = false)
  @Getter
  private Instant expiredAt;

  @Column(nullable = false)
  @Getter
  private ScheduleMessageDataContainerDto scheduleMessageDataContainerDto;

  @Column(nullable = false)
  @Getter
  private DeviceRequest deviceRequest;
}
