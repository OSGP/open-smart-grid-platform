package org.opensmartgridplatform.core.infra.jms.protocol.outbound.serialization;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.*;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class CreateSerializedObjectsTest {

  /**
   * Serializes multiple objects and writes them to their own file.
   *
   * <p>The generated files serve as a fixture for the {@code RequestDeserializationTest} in the
   * gxf-publiclighting-message-transformer project, allowing that test to verify deserialization
   * compatibility across projects.
   */
  @Test
  void testSetScheduleRequest() {

    var entry1 = new ScheduleEntryDto();
    entry1.setIsEnabled(true);
    entry1.setLightValue(List.of(new LightValueDto(2, true, null)));
    entry1.setWeekDay(WeekDayTypeDto.ALL);
    entry1.setTriggerType(TriggerTypeDto.ASTRONOMICAL);
    entry1.setActionTime(ActionTimeTypeDto.SUNSET);

    var entry2 = new ScheduleEntryDto();
    entry2.setIsEnabled(true);
    entry2.setLightValue(List.of(new LightValueDto(2, false, null)));
    entry2.setWeekDay(WeekDayTypeDto.ALL);
    entry2.setTriggerType(TriggerTypeDto.ASTRONOMICAL);
    entry2.setActionTime(ActionTimeTypeDto.SUNRISE);

    var schedule = new ScheduleDto(List.of(entry1, entry2));

    var request = new RequestMessage("corr_id", "org_id", "dvc_id", schedule);
    createOutputFile(request, "set-schedule-request.ser");
  }

  @Test
  void testEventNotificationRequest() {
    final String deviceUid = "testUid";
    final ZonedDateTime dateTime = ZonedDateTime.parse("2026-01-01T12:00:00+00:00");
    final EventTypeDto eventTypeDto = EventTypeDto.LIGHT_SENSOR_REPORTS_LIGHT;
    final String description = "Sensor reports light";
    final Integer index = 0;
    final EventNotificationDto eventNotificationDto =
        new EventNotificationDto(deviceUid, dateTime, eventTypeDto, description, index);
    var request = new RequestMessage("corr_id", "org_id", "dvc_id", eventNotificationDto);
    createOutputFile(request, "event-notification-request.ser");
  }

  private void createOutputFile(RequestMessage request, String filename) {
    Path output = Path.of("src", "test", "resources", filename);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(output.toFile()))) {
      oos.writeObject(request.getRequest());
    } catch (Exception ex) {
      System.out.println("exception occurred:" + ex.getMessage());
    }
  }
}
