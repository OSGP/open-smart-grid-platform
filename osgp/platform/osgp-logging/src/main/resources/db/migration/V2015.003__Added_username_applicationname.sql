-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--
-- Add user_name and application_name column to web_service_monitor_log table
--

ALTER TABLE web_service_monitor_log ADD COLUMN user_name character varying(40),ADD COLUMN application_name character varying(40);

UPDATE web_service_monitor_log SET user_name = 'XX', application_name='XX' ;

ALTER TABLE web_service_monitor_log ALTER COLUMN user_name SET NOT NULL, ALTER COLUMN application_name SET NOT NULL ;