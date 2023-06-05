-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--
-- Add data size column to oslp_log_item table
--

ALTER TABLE oslp_log_item ADD COLUMN data_size integer;

--
-- Set an invalid size for the records created before this added column.
--

UPDATE oslp_log_item SET data_size=-1;

--
-- Create NOT NULL constraint for this added column.
--

ALTER TABLE oslp_log_item ALTER COLUMN data_size SET NOT NULL;