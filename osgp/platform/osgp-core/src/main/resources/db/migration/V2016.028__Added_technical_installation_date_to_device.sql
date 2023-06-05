-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE device ADD COLUMN technical_installation_date timestamp without time zone;


UPDATE device SET technical_installation_date = LOCALTIMESTAMP WHERE is_activated = TRUE;