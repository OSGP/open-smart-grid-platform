package org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Date expiredAt;

    @Column(nullable = false)
    @Getter
    private ScheduleMessageDataContainerDto scheduleMessageDataContainerDto;

    @Column(nullable = false)
    @Getter
    private DeviceRequest deviceRequest;
}
