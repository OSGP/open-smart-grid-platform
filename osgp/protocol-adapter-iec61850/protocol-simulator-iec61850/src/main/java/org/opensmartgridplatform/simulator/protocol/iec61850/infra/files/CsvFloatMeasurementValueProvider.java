package org.opensmartgridplatform.simulator.protocol.iec61850.infra.files;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensmartgridplatform.simulator.protocol.iec61850.domain.exceptions.EndOfSimulationException;
import org.opensmartgridplatform.simulator.protocol.iec61850.domain.services.ValueProvider;
import org.opensmartgridplatform.simulator.protocol.iec61850.domain.valueobjects.FloatMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvFloatMeasurementValueProvider implements ValueProvider<FloatMeasurement> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFloatMeasurementValueProvider.class);

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(DATE_TIME_PATTERN)
            .toFormatter();

    private static final Function<String[], FloatMeasurement> TO_FLOAT_MEASUREMENT = s -> new FloatMeasurement(
            LocalDateTime.parse(s[0], DATE_TIME_FORMATTER)
                    .atZone(ZoneOffset.UTC),
            Float.parseFloat(s[1]));

    private final String filename;

    private List<FloatMeasurement> values;
    private int counter = 0;

    public CsvFloatMeasurementValueProvider(final String filename) {
        this.filename = filename;
        this.initData();
    }

    @Override
    public FloatMeasurement getValue() throws EndOfSimulationException {
        if (this.counter >= this.values.size()) {
            throw new EndOfSimulationException();
        }
        final FloatMeasurement result = this.values.get(this.counter);
        this.counter += 1;
        return result;
    }

    private void initData() {
        final File file = new File(this.filename);
        if (file.exists()) {
            LOGGER.info("Loading values from file: {}", this.filename);
            this.loadFileFromPath();
        } else {
            LOGGER.info("Loading values from class path: {}", this.filename);
            this.loadFileFromClassPath();
        }
    }

    private void loadFileFromPath() {
        try (final Stream<String> stream = Files.lines(Paths.get(this.filename))) {
            this.loadData(stream);
            LOGGER.info("Read {} values from file: {}", this.values.size(), this.filename);
        } catch (final Exception e) {
            LOGGER.error("Exception while reading values from file: ", e);
        }
    }

    private void loadFileFromClassPath() {
        try (final Stream<String> stream = Files.lines(Paths.get(this.getClass()
                .getClassLoader()
                .getResource(this.filename)
                .toURI()))) {
            this.loadData(stream);
            LOGGER.info("Read {} values from file on class path: {}", this.values.size(), this.filename);
        } catch (final Exception e) {
            LOGGER.error("Exception while reading values from file: ", e);
        }
    }

    private void loadData(final Stream<String> stream) {
        this.values = stream.skip(1)
                .map(s -> s.split(";"))
                .map(TO_FLOAT_MEASUREMENT)
                .collect(Collectors.toList());
    }
}
