-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE periodic_meter_data RENAME TO periodic_meter_reads;

ALTER TABLE meter_data RENAME TO meter_reads;

ALTER TABLE meter_reads RENAME periodic_meter_data_id to periodic_meter_reads_id ;

