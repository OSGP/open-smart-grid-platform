-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'iec61850_last_report_entry') THEN
  CREATE TABLE IF NOT EXISTS iec61850_last_report_entry (
    id bigserial,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL,
    report_id character varying(255) NOT NULL,
    entry_id bytea NOT NULL,
    time_of_entry timestamp without time zone NOT NULL,
    CONSTRAINT iec61850_last_report_entry_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_last_report_entry_ukey UNIQUE (device_identification, report_id)
  );

  ALTER TABLE iec61850_last_report_entry OWNER TO osp_admin;

END IF;

END;
$$