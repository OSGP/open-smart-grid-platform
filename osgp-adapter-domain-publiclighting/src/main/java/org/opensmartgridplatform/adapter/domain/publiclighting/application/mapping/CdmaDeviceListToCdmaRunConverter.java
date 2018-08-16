package org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaRun;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class CdmaDeviceListToCdmaRunConverter extends CustomConverter<List<CdmaDevice>, CdmaRun> {

    @Override
    public CdmaRun convert(final List<CdmaDevice> cdmaDeviceListSource,
            final Type<? extends CdmaRun> cdmaRunDestination, final MappingContext mappingContext) {

        final Stream<CdmaDevice> nonEmptyDevices = cdmaDeviceListSource.stream().map(CdmaDevice::mapEmptyFields);

        final Map<Boolean, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>> rawCdmaRun = nonEmptyDevices
                .collect(this.partitionMastSegmentCollector());

        final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegments = rawCdmaRun.get(Boolean.TRUE);
        final List<CdmaMastSegment> cdmaMastSegments = null;

        final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawEmptyMastSegment = rawCdmaRun.get(Boolean.FALSE);
        final TreeMap<Short, List<CdmaDevice>> rawBatchesWithoutMastSegment = rawEmptyMastSegment
                .get(CdmaDevice.DEFAULT_MASTSEGMENT);
        final List<CdmaBatch> batchesWithoutMastSegment = null;

        return new CdmaRun(cdmaMastSegments, batchesWithoutMastSegment);
    }

    /* @formatter:off
    public List<CdmaRun> from(final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegments) {
        final List<CdmaRun> cdmaMastSegments = new ArrayList<>();
        for (final Entry<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegment : rawMastSegments.entrySet()) {
            final CdmaRun cdmaMastSegment = new CdmaRun(rawMastSegment.getKey(), rawMastSegment.getValue());
        }

        return cdmaMastSegments;
    }
    * @formatter:on
    */

    private List<CdmaBatch> toCdmaBatch(final TreeMap<Short, List<CdmaDevice>> batchesMap) {
        return batchesMap.entrySet().stream().map(entry -> new CdmaBatch(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /*
     * Returns a collector which can create a map by dividing a stream of CDMA
     * mast segments over items having the default CDMA mast segment and items
     * having another CDMA mast segment.
     *
     * Example: (Boolean.TRUE, [(DEVICE-WITHOUT-MAST-SEGMENT, [(1, [cd6,
     * cd1])])], Boolean.FALSE, [(2500/1, [(1, [cd6, cd1]), (2, [cd24, cd21])]),
     * ....])
     */
    private Collector<CdmaDevice, ?, Map<Boolean, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>>> partitionMastSegmentCollector() {
        return Collectors.partitioningBy(CdmaDevice::hasDefaultMastSegment, this.mastSegmentCollector());
    }

    /*
     * Returns a collector which can create a map by grouping a stream of CDMA
     * device batches by their mast segment. The created map is ordered by mast
     * segment. The batches within a mast segment are ordered by batch number.
     *
     * Example: (2500/1, [(1, [cd6, cd1]), (2, [cd24, cd21])]), (2500/2, [(1,
     * [cd12, cd13]), (3, [cd20])]), ...
     */
    private Collector<CdmaDevice, ?, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>> mastSegmentCollector() {
        return Collectors.groupingBy(CdmaDevice::getMastSegmentName, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>::new,
                this.batchNumberCollector());
    }

    /*
     * Returns a collector which can create a map by grouping a stream of CDMA
     * devices by their batch number. The created map is ordered by batch
     * number.
     *
     * Example: (1, [CdmaDevice6, CdmaDevice1]), (2, [CdmaDevice24,
     * CdmaDevice21]), (5, [CdmaDevice12, CdmaDevice15]), ...
     */
    private Collector<CdmaDevice, ?, TreeMap<Short, List<CdmaDevice>>> batchNumberCollector() {
        return Collectors.groupingBy(CdmaDevice::getBatchNumber, TreeMap<Short, List<CdmaDevice>>::new,
                Collectors.toList());
    }
}
