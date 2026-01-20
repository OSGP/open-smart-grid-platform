package org.opensmartgridplatform.core.infra.jms.protocol.outbound.serialization;

import static java.util.Arrays.asList;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
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
  void testConfigurationRequest() {
    final ConfigurationDto configurationDto =
        new ConfigurationDto.Builder()
            .withLightType(LightTypeDto.RELAY)
            .withDaliConfiguration(createDaliConfigurationDto())
            .withRelayConfiguration(createRelayConfigurationDto())
            .withPreferredLinkType(LinkTypeDto.CDMA)
            .build();
    configurationDto.setTimeSyncFrequency(133);
    configurationDto.setDeviceFixedIp(new DeviceFixedIpDto("ipAddress1", "netMask1", "gateWay1"));
    configurationDto.setDhcpEnabled(true);
    configurationDto.setTlsEnabled(true);
    configurationDto.setTlsPortNumber(134);
    configurationDto.setCommonNameString("commonNameString1");
    configurationDto.setCommunicationTimeout(135);
    configurationDto.setCommunicationNumberOfRetries(136);
    configurationDto.setCommunicationPauseTimeBetweenConnectionTrials(137);
    configurationDto.setOsgpIpAddress("osgpIpAddress1");
    configurationDto.setOsgpPortNumber(138);
    configurationDto.setNtpHost("ntpHost1");
    configurationDto.setNtpEnabled(true);
    configurationDto.setNtpSyncInterval(139);
    configurationDto.setTestButtonEnabled(true);
    configurationDto.setAutomaticSummerTimingEnabled(true);
    configurationDto.setAstroGateSunRiseOffset(140);
    configurationDto.setAstroGateSunSetOffset(141);
    configurationDto.setSwitchingDelays(asList(142, 143));
    configurationDto.setRelayLinking(createRelayMatrixDto());
    configurationDto.setRelayRefreshing(true);
    configurationDto.setSummerTimeDetails(
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(146L * 24 * 60 * 60 * 1000), ZoneId.systemDefault()));
    configurationDto.setWinterTimeDetails(
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(147L * 24 * 60 * 60 * 1000), ZoneId.systemDefault()));

    var request = new RequestMessage("corr_id", "org_id", "dvc_id", configurationDto);
    createOutputFile(request, "configuration-request.ser");
  }

  private void createOutputFile(RequestMessage request, String filename) {
    Path output = Path.of("src", "test", "resources", filename);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(output.toFile()))) {
      oos.writeObject(request.getRequest());
    } catch (Exception ex) {
      System.out.println("exception occurred:" + ex.getMessage());
    }
  }

  private DaliConfigurationDto createDaliConfigurationDto() {
    return new DaliConfigurationDto(123, this.createIndexAddressMap());
  }

  private HashMap<Integer, Integer> createIndexAddressMap() {
    final HashMap<Integer, Integer> map = new HashMap<>();
    map.put(124, 125);
    map.put(125, 126);
    return map;
  }

  private RelayConfigurationDto createRelayConfigurationDto() {
    return new RelayConfigurationDto(
        asList(
            new RelayMapDto(127, 128, RelayTypeDto.LIGHT, "alias1"),
            new RelayMapDto(129, 130, RelayTypeDto.TARIFF, "alias2")));
  }

  private List<RelayMatrixDto> createRelayMatrixDto() {
    return asList(new RelayMatrixDto(144, true), new RelayMatrixDto(145, false));
  }
}
