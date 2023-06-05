-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--
-- Add enabled column to organisation table
--

ALTER TABLE organisation ADD COLUMN enabled boolean;

UPDATE organisation SET enabled=TRUE;

ALTER TABLE organisation ALTER COLUMN enabled SET NOT NULL;