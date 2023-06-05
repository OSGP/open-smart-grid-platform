-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- 
-- Rename request_correlation_uid column
--
ALTER TABLE web_service_monitor_log RENAME COLUMN request_correlation_uid TO correlation_uid;