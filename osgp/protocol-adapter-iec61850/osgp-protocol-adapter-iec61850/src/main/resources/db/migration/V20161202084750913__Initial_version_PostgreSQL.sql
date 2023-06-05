-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO $$
BEGIN

  CREATE TABLE IF NOT EXISTS iec61850_device (
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL,
    icd_filename character varying(255),
    port integer,
    CONSTRAINT iec61850_device_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_device_device_identification_key UNIQUE (device_identification)
  );

  ALTER TABLE iec61850_device OWNER TO osp_admin;

END$$;
