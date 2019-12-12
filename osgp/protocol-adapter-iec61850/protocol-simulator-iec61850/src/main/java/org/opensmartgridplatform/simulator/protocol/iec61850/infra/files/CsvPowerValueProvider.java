package org.opensmartgridplatform.simulator.protocol.iec61850.infra.files;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensmartgridplatform.simulator.protocol.iec61850.domain.services.ValueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "rtu.transformer.simulation.enabled", havingValue = "true")
public class CsvPowerValueProvider implements ValueProvider<Float>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvPowerValueProvider.class);

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(DATE_TIME_PATTERN)
            .toFormatter();

    private static final Function<String[], ZonedDateTime> ZONED_DATE_TIME_KEY = s -> ZonedDateTime.parse(s[0],
            DATE_TIME_FORMATTER);

    private static final Function<String[], Float> FLOAT_VALUE = s -> Float.parseFloat(s[1]);

    @Value("${rtu.transformer.power.file:transformer-p.csv}")
    private String filename;

    private Map<ZonedDateTime, Float> valuesMap;

    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private long intervalInSeconds;

    @Override
    public Float getValue() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
        final ZonedDateTime current = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this.getMeasurementTime(now)),
                ZoneOffset.UTC);
        if (current.isBefore(this.startDateTime)) {
            LOGGER.warn("Current date/time before available values");
            return this.valuesMap.get(this.startDateTime);
        } else if (current.isAfter(this.endDateTime)) {
            LOGGER.warn("Current date/time after available values");
            return this.valuesMap.get(this.endDateTime);
        } else {
            return this.valuesMap.get(current);
        }
    }

    private long getMeasurementTime(final ZonedDateTime dateTime) {
        final long seconds = dateTime.toEpochSecond();
        return seconds - seconds % this.intervalInSeconds;
        // return Math.round(seconds/intervalInSeconds) * intervalInSeconds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initData();
    }

    private void initData() {
        try (Stream<String> stream = Files.lines(Paths.get(this.filename))) {
            this.valuesMap = stream.skip(1)
                    .map(s -> s.split(";"))
                    .collect(Collectors.toMap(ZONED_DATE_TIME_KEY, FLOAT_VALUE));

            this.startDateTime = this.valuesMap.keySet()
                    .stream()
                    .min(Comparator.comparing(Function.identity(), (s1, s2) -> s1.compareTo(s2)))
                    .orElseThrow(Exception::new);

            this.endDateTime = this.valuesMap.keySet()
                    .stream()
                    .max(Comparator.comparing(Function.identity(), (s1, s2) -> s1.compareTo(s2)))
                    .orElseThrow(Exception::new);

            this.intervalInSeconds = (this.endDateTime.toEpochSecond() - this.startDateTime.toEpochSecond())
                    / (this.valuesMap.size() - 1);

        } catch (final Exception e) {
            LOGGER.error("Exception while reading power values from file: ", e);
        }
    }

}
