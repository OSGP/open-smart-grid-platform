-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE INDEX IF NOT EXISTS permit_created_at_idx ON permit (created_at);
