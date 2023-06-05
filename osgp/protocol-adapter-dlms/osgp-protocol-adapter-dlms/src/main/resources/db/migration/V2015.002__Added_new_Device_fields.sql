-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE dlms_device ADD COLUMN icc_id character varying(20) NOT NULL;

ALTER TABLE dlms_device ADD COLUMN communication_provider character varying(10) NOT NULL;

ALTER TABLE dlms_device ADD COLUMN communication_method character varying(10) NOT NULL;

ALTER TABLE dlms_device ADD COLUMN master_key character varying(255) NOT NULL;

ALTER TABLE dlms_device ADD COLUMN global_encryption_unicast_key character varying(255) NOT NULL;

ALTER TABLE dlms_device ADD COLUMN authentication_key character varying(255) NOT NULL;

ALTER TABLE dlms_device ADD COLUMN hls3active boolean NOT NULL;

ALTER TABLE dlms_device ADD COLUMN hls4active boolean NOT NULL;

ALTER TABLE dlms_device ADD COLUMN hls5active boolean NOT NULL;
