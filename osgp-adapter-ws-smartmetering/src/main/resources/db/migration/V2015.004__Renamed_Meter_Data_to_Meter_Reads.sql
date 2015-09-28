ALTER TABLE periodic_meter_data RENAME TO periodic_meter_reads;

ALTER TABLE meter_data RENAME TO meter_reads;

ALTER TABLE meter_reads RENAME periodic_meter_data_id to periodic_meter_reads_id ;

