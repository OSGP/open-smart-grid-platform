package com.alliander.osgp.domain.core.repositories;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.valueobjects.ScheduledTaskStatusType;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {

    List<ScheduledTask> findByScheduledTimeLessThan(Timestamp currentTimestamp);

    List<ScheduledTask> findByStatusAndScheduledTimeLessThan(ScheduledTaskStatusType status, Timestamp currentTimestamp);

    List<ScheduledTask> findAllByStatus(ScheduledTaskStatusType status);

    List<ScheduledTask> findByStatusNot(ScheduledTaskStatusType status);

    List<ScheduledTask> findByDeviceIdentification(String deviceIdentification);

    List<ScheduledTask> findByOrganisationIdentification(String organisationIdentification);

    ScheduledTask findByCorrelationUid(String correlationUid);

}
