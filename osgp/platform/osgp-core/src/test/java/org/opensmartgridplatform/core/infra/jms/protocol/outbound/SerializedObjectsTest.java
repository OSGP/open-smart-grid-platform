package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.ActionTimeTypeDto;
import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightValueDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto;
import org.opensmartgridplatform.dto.valueobjects.TriggerTypeDto;
import org.opensmartgridplatform.dto.valueobjects.WeekDayTypeDto;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class SerializedObjectsTest {

  @Test
  void testGetStatusMessage() {
    var request = new RequestMessage("corr_id", "org_id", "dvc_id", DomainTypeDto.PUBLIC_LIGHTING);
    try (ObjectOutputStream oos =
        new ObjectOutputStream(new FileOutputStream("get-status-request.ser"))) {
      oos.writeObject(request.getRequest());
    } catch (Exception ex) {
      System.out.println("exception occurred:" + ex.getMessage());
    }
  }

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
    try (ObjectOutputStream oos =
        new ObjectOutputStream(new FileOutputStream("set-schedule-request.ser"))) {
      oos.writeObject(request.getRequest());
    } catch (Exception ex) {
      System.out.println("exception occurred:" + ex.getMessage());
    }
  }
}
